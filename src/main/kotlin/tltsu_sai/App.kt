import javafx.application.Platform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.letsPlot.geom.geom_histogram
import jetbrains.letsPlot.geom.geom_point
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.lets_plot
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.random.Random

private val SVG_COMPONENT_FACTORY_JFX =
        { svg: SvgSvgElement -> SceneMapperJfxPanel(svg, listOf(Style.JFX_PLOT_STYLESHEET)) }

private val JFX_EDT_EXECUTOR = { runnable: () -> Unit ->
    if (Platform.isFxApplicationThread()) {
        runnable.invoke()
    } else {
        Platform.runLater(runnable)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val source = Service.getSourceData()
        val data = Service.prepareDataForPlot(source)

        // Create plot specs using Lets-Plot Kotlin API
        val p = lets_plot(data) {x = "x"; y = "y"; color = "city"} + geom_point(data)

        // Create JFXPanel showing the plot.
        val plotSpec = p.toSpec()
        val plotSize = DoubleVector(600.0, 300.0)

        val component =
                MonolithicAwt.buildPlotFromRawSpecs(plotSpec, plotSize, SVG_COMPONENT_FACTORY_JFX, JFX_EDT_EXECUTOR) {
                    for (message in it) {
                        println("PLOT MESSAGE: $message")
                    }
                }


        // Show plot in Swing frame.
        val frame1 = JFrame("The Minimal")
        frame1.contentPane.add(component)
        frame1.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame1.pack()
        frame1.isVisible = true
    }
//    println("Евклидово расстояние")
//    Service.processKmeans(true)
//
//    println("\n\nРасстояние Манхэттена")
//    Service.processKmeans(false)
}
