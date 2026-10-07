param(
    [string]$DependencyDirectory = (Join-Path $env:TEMP 'ife-java-check'),
    [string]$Java = 'java'
)
$ErrorActionPreference = 'Stop'
$project = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$build = Join-Path $project ('target/jar-' + [Guid]::NewGuid().ToString('N'))
$classes = Join-Path $build 'classes'
$builder = Join-Path $build 'builder'
$distribution = Join-Path $project 'dist'
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
New-Item -ItemType Directory -Force -Path $classes, $builder, $distribution | Out-Null
$sources = (Get-ChildItem (Join-Path $project 'src/main/java') -Recurse -Filter '*.java').FullName
& $Java -jar (Join-Path $DependencyDirectory 'ecj.jar') -1.8 -encoding UTF-8 -nowarn -classpath ($jars -join ';') -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'Application compilation failed' }
Copy-Item -Path (Join-Path $project 'src/main/resources/*') -Destination $classes -Recurse -Force
& $Java -jar (Join-Path $DependencyDirectory 'ecj.jar') -1.8 -encoding UTF-8 -nowarn -d $builder (Join-Path $PSScriptRoot 'BuildJar.java')
if ($LASTEXITCODE -ne 0) { throw 'JAR builder compilation failed' }
$jar = Join-Path $build 'ife-012.jar'
& $Java -cp $builder BuildJar $jar $classes $jars
if ($LASTEXITCODE -ne 0) { throw 'JAR packaging failed' }
Copy-Item -LiteralPath $jar -Destination (Join-Path $distribution 'ife-012.jar') -Force
Write-Output "Built: $distribution/ife-012.jar"
