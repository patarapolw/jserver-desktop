package r2r

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.swt.SWT
import org.eclipse.swt.browser.Browser
import org.eclipse.swt.browser.TitleListener
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import java.io.File
import java.net.ServerSocket
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import kotlin.concurrent.thread

data class Config (
    val title: String,
    val server: List<String>,
    val port: Int,
    val maximized: Boolean?
)

fun main() {
    val configJsonURI = Config::class.java.classLoader.getResource("config.json")?.toURI()
        ?: throw Error("config.json is not found.")
    val resourceFile = File(configJsonURI).parentFile

    val config = ObjectMapper().readValue(File(configJsonURI), Config::class.java)

    val display = Display()
    val shell = Shell(display)
    shell.maximized = config.maximized ?: false
    shell.text = config.title

    val layout = FillLayout()

    val browser = Browser(shell, SWT.FILL)
    browser.addTitleListener { TitleListener {
        shell.text = it.title
    } }

    shell.layout = layout
    shell.open()

    val pb = ProcessBuilder(config.server)
    pb.directory(resourceFile)

    val env = pb.environment()
    env["PORT"] = env["PORT"] ?: config.port.toString()
    env["TOKEN"] = env["TOKEN"] ?: {
        val keyGenerator = KeyGenerator.getInstance("AES")
        val rng = SecureRandom.getInstanceStrong()
        keyGenerator.init(rng)

        keyGenerator.generateKey().toString()
    }()

    val serverProcess = pb.start()
    val pid = {
        try {
            val field = serverProcess::class.java.getDeclaredField("pid")
            field.isAccessible = true

            field.getInt(serverProcess)
        } catch (e: Error) {
            null
        }
    }()

    println("Starting the server with token ${env["TOKEN"]}${pid?.let {
        " and PID $it"
    } ?: ""}")

    thread {
        while (true) {
            try {
                ServerSocket(env["PORT"]!!.toInt())
                break
            } catch (e: Error) {
                Thread.sleep(1000)
            }
        }
        Display.getDefault().asyncExec {
            browser.url = "http://localhost:${env["PORT"]}?token=${env["TOKEN"]}"
        }
    }

    shell.addDisposeListener {
        serverProcess.destroy()
    }

    while (!shell.isDisposed) {
        if (!display.readAndDispatch()) {
            display.sleep();
        }
    }
}