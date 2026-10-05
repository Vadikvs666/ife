param(
    [string]$DependencyDirectory = (Join-Path $env:TEMP 'ife-java-check'),
    [string]$Java = 'java',
    [ValidatePattern('^[a-zA-Z0-9_-]+$')]
    [string]$OutputName = 'IFE-windows'
)
$ErrorActionPreference = 'Stop'
$project = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$build = Join-Path $project ('target/windows-' + [Guid]::NewGuid().ToString('N'))
$classes = Join-Path $build 'classes'
$distribution = Join-Path $project ('dist/' + $OutputName)
$dependencies = @(
    'httpclient-4.5.1.jar', 'httpcore-4.4.3.jar', 'httpmime-4.5.1.jar', 'commons-logging-1.2.jar',
    'poi-3.17.jar', 'poi-ooxml-3.17.jar', 'poi-ooxml-schemas-3.17.jar',
    'xmlbeans-2.6.0.jar', 'commons-collections4-4.1.jar', 'commons-codec-1.10.jar',
    'curvesapi-1.04.jar', 'javax.mail-1.6.0.jar', 'json-simple-1.1.1.jar',
    'commons-io-2.2.jar', 'mysql-connector-java-8.0.11.jar', 'protobuf-java-2.6.0.jar'
)
$jars = @($dependencies | ForEach-Object {
    $path = Join-Path $DependencyDirectory $_
    if (-not (Test-Path -LiteralPath $path)) { throw "Missing dependency: $path" }
    $path
})
New-Item -ItemType Directory -Force -Path $classes, (Join-Path $distribution 'lib') | Out-Null
$sources = (Get-ChildItem (Join-Path $project 'src/main/java') -Recurse -Filter '*.java').FullName
& $Java -jar (Join-Path $DependencyDirectory 'ecj.jar') -1.8 -encoding UTF-8 -nowarn -classpath ($jars -join ';') -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed' }
Copy-Item -Path (Join-Path $project 'src/main/resources/*') -Destination $classes -Recurse -Force
Copy-Item -LiteralPath $jars -Destination (Join-Path $distribution 'lib') -Force

$manifestDirectory = Join-Path $classes 'META-INF'
New-Item -ItemType Directory -Force -Path $manifestDirectory | Out-Null
$manifest = "Manifest-Version: 1.0`r`nMain-Class: vadikvs.ife.MainApp`r`n"
$classPath = 'Class-Path: ' + (($dependencies | ForEach-Object { 'lib/' + $_ }) -join ' ')
while ($classPath.Length -gt 70) {
    $manifest += $classPath.Substring(0, 70) + "`r`n"
    $classPath = ' ' + $classPath.Substring(70)
}
$manifest += $classPath + "`r`n`r`n"
[IO.File]::WriteAllText((Join-Path $manifestDirectory 'MANIFEST.MF'), $manifest, [Text.Encoding]::ASCII)
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
function New-PortableZip([string]$Source, [string]$Destination) {
    $archive = [IO.Compression.ZipFile]::Open($Destination, [IO.Compression.ZipArchiveMode]::Create)
    try {
        foreach ($file in Get-ChildItem -LiteralPath $Source -Recurse -File) {
            # JAR resource paths must use forward slashes, even on older .NET builds.
            $name = $file.FullName.Substring($Source.Length + 1).Replace('\', '/')
            [IO.Compression.ZipFileExtensions]::CreateEntryFromFile(
                $archive, $file.FullName, $name, [IO.Compression.CompressionLevel]::Optimal) | Out-Null
        }
    } finally {
        $archive.Dispose()
    }
}
$jar = Join-Path $build 'ife.jar'
New-PortableZip $classes $jar
Copy-Item -LiteralPath $jar -Destination (Join-Path $distribution 'ife.jar') -Force

$compiler = Join-Path $env:WINDIR 'Microsoft.NET/Framework64/v4.0.30319/csc.exe'
& $compiler /nologo /target:winexe /platform:anycpu /optimize+ /reference:System.Windows.Forms.dll "/out:$distribution/IFE.exe" (Join-Path $PSScriptRoot 'Launcher.cs')
if ($LASTEXITCODE -ne 0) { throw 'Windows launcher compilation failed' }
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'START.txt') -Destination $distribution -Force
Copy-Item -LiteralPath (Join-Path $project 'README.md') -Destination $distribution -Force
$zip = Join-Path $build 'IFE-windows.zip'
New-PortableZip $distribution $zip
Copy-Item -LiteralPath $zip -Destination (Join-Path $project ('dist/' + $OutputName + '.zip')) -Force
Write-Output "Built: $distribution/IFE.exe"
Write-Output "Archive: $project/dist/$OutputName.zip"
