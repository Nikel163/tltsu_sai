import javafx.application.Platform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.letsPlot.GGBunch
import jetbrains.letsPlot.geom.geom_histogram
import jetbrains.letsPlot.geom.geom_point
import jetbrains.letsPlot.geom.geom_text
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
        val data = Service.prepareDataForPlot(table())

        // Create plot specs using Lets-Plot Kotlin API
        val p = lets_plot(data) {x = "x"; y = "y"; color = "cluster"} + geom_point(data) //+ geom_text(data) {label = "index"; }
        // Create JFXPanel showing the plot.
        val plotSpec = p.toSpec()
        val plotSize = DoubleVector(600.0, 300.0)

        val component = MonolithicAwt.buildPlotFromRawSpecs(plotSpec, plotSize, SVG_COMPONENT_FACTORY_JFX, JFX_EDT_EXECUTOR) {}


        // Show plot in Swing frame.
        val frame1 = JFrame("The Minimal")
        frame1.contentPane.add(component)
        frame1.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame1.setLocationRelativeTo(null)
        frame1.pack()
        frame1.isVisible = true
    }
//    println("Евклидово расстояние")
//    Service.processKmeans(true)
//
//    println("\n\nРасстояние Манхэттена")
//    Service.processKmeans(false)
}

fun table(): HashMap<String, ParamDTO> {
    return hashMapOf(
            "A"   to ParamDTO(0.0, 1),
            "B"   to ParamDTO(0.0, 1),
            "C"   to ParamDTO(0.0, 1),
            "D"   to ParamDTO(0.0, 1),
            "E"   to ParamDTO(0.0, 1),
            "F"   to ParamDTO(0.0, 1),
            "G"   to ParamDTO(0.0, 1),
            "H"   to ParamDTO(0.0, 1),
            "I"   to ParamDTO(0.0, 1),
            "J"   to ParamDTO(0.0, 1),
            "K"   to ParamDTO(0.0, 1),
            "L"   to ParamDTO(0.0, 1),
            "M"   to ParamDTO(0.0, 1),
            "N"   to ParamDTO(0.0, 1),
            "O"   to ParamDTO(0.0, 1),
            "P"   to ParamDTO(0.0, 1),
            "Q"   to ParamDTO(0.0, 1),
            "R"   to ParamDTO(0.0, 1),
            "S"   to ParamDTO(0.0, 1),
            "T"   to ParamDTO(0.0, 1),
            "U"   to ParamDTO(0.0, 1),
            "V"   to ParamDTO(0.0, 1),
            "W"   to ParamDTO(0.0, 1),
            "X"   to ParamDTO(0.0, 1),
            "Y"   to ParamDTO(0.0, 1),
            "Z"   to ParamDTO(0.0, 1),
            "AA"  to ParamDTO(0.0, 1),
            "AB"  to ParamDTO(0.0, 2),
            "AC"  to ParamDTO(0.0, 2),
            "AD"  to ParamDTO(0.0, 2),
            "AE"  to ParamDTO(0.0, 2),
            "AF"  to ParamDTO(0.0, 2),
            "AG"  to ParamDTO(0.0, 2),
            "AH"  to ParamDTO(0.0, 2),
            "AI"  to ParamDTO(0.0, 2),
            "AJ"  to ParamDTO(0.0, 2),
            "AK"  to ParamDTO(0.0, 2),
            "AL"  to ParamDTO(0.0, 2),
            "AM"  to ParamDTO(0.0, 2),
            "AN"  to ParamDTO(0.0, 2),
            "AO"  to ParamDTO(0.0, 3),
            "AP"  to ParamDTO(0.0, 3),
            "AQ"  to ParamDTO(0.0, 3),
            "AR"  to ParamDTO(0.0, 3),
            "AS"  to ParamDTO(0.0, 3),
            "AT"  to ParamDTO(0.0, 3),
            "AU"  to ParamDTO(0.0, 3),
            "AV"  to ParamDTO(0.0, 3),
            "AW"  to ParamDTO(0.0, 3),
            "AX"  to ParamDTO(0.0, 3),
            "AY"  to ParamDTO(0.0, 3),
            "AZ"  to ParamDTO(0.0, 3)
    )
}
