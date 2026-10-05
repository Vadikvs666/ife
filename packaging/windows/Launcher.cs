using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;
using Microsoft.Win32;

// Small Windows launcher; the application itself remains Java/JavaFX.
internal static class Launcher
{
    private static string JavaIn(string home)
    {
        if (String.IsNullOrEmpty(home)) return null;
        foreach (string root in new[] { home, Path.Combine(home, "jre") })
        {
            string java = Path.Combine(root, "bin", "java.exe");
            if (File.Exists(java) && File.Exists(Path.Combine(root, "lib", "ext", "jfxrt.jar")))
                return java;
        }
        return null;
    }

    private static string FindJava(string directory)
    {
        var homes = new List<string>();
        homes.Add(Path.Combine(directory, "runtime"));
        homes.Add(Environment.GetEnvironmentVariable("JAVA_HOME"));
        foreach (RegistryView view in new[] { RegistryView.Registry64, RegistryView.Registry32 })
        {
            using (RegistryKey root = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, view))
            using (RegistryKey java = root.OpenSubKey(@"SOFTWARE\JavaSoft\Java Runtime Environment"))
            {
                if (java == null) continue;
                foreach (string version in java.GetSubKeyNames())
                    using (RegistryKey release = java.OpenSubKey(version))
                        homes.Add(release.GetValue("JavaHome") as string);
            }
        }
        foreach (string programFiles in new[] { Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
                Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86) })
        {
            string javaFolder = Path.Combine(programFiles, "Java");
            if (Directory.Exists(javaFolder)) homes.AddRange(Directory.GetDirectories(javaFolder));
        }
        foreach (string home in homes)
        {
            string java = JavaIn(home);
            if (java != null) return java;
        }
        return null;
    }

    [STAThread]
    private static int Main(string[] args)
    {
        bool check = args.Length == 1 && args[0] == "--check-runtime";
        try
        {
            string directory = AppDomain.CurrentDomain.BaseDirectory;
            string java = FindJava(directory);
            if (java == null) throw new InvalidOperationException(
                "Для запуска нужна Java 8 с JavaFX. Установите её или укажите папку в JAVA_HOME.");
            string jar = Path.Combine(directory, "ife.jar");
            if (!File.Exists(jar)) throw new FileNotFoundException(
                "Не найден ife.jar. Распакуйте всю папку приложения рядом с IFE.exe.");

            string logDirectory = Path.Combine(Environment.GetFolderPath(
                Environment.SpecialFolder.LocalApplicationData), "IFE", "logs");
            Directory.CreateDirectory(logDirectory);
            string log = Path.Combine(logDirectory, "ife-" + DateTime.Now.ToString("yyyyMMdd-HHmmss-fff") + ".log");
            using (var output = new StreamWriter(log))
            using (var process = new Process())
            {
                output.AutoFlush = true;
                process.StartInfo = new ProcessStartInfo(java,
                    check ? "-version" : "-Dfile.encoding=UTF-8 -jar \"" + jar + "\"");
                process.StartInfo.WorkingDirectory = directory;
                process.StartInfo.UseShellExecute = false;
                process.StartInfo.CreateNoWindow = true;
                process.StartInfo.RedirectStandardOutput = true;
                process.StartInfo.RedirectStandardError = true;
                process.OutputDataReceived += delegate(object sender, DataReceivedEventArgs e) {
                    if (e.Data != null) lock (output) output.WriteLine(e.Data);
                };
                process.ErrorDataReceived += delegate(object sender, DataReceivedEventArgs e) {
                    if (e.Data != null) lock (output) output.WriteLine(e.Data);
                };
                process.Start();
                process.BeginOutputReadLine();
                process.BeginErrorReadLine();
                process.WaitForExit();
                if (process.ExitCode != 0 && !check)
                    MessageBox.Show("Приложение завершилось с ошибкой. Журнал: " + log,
                        "IFE", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return process.ExitCode;
            }
        }
        catch (Exception ex)
        {
            if (!check) MessageBox.Show(ex.Message, "IFE", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return 1;
        }
    }
}
