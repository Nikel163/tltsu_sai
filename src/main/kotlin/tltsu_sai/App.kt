import javafx.application.Platform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicAwt
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import jetbrains.letsPlot.geom.geom_point
import jetbrains.letsPlot.geom.geom_text
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.lets_plot
import java.math.BigDecimal
import java.math.RoundingMode
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
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
        println("Евклидово расстояние")
        processKmeans(true)

//        println("\n\nРасстояние Манхэттена")
//        processKmeans(false)
    }
}

private const val NUMBER_OF_CLUSTERS = 3

fun getSourceData(): List<DataDTO> {
    return listOf(
            DataDTO("A", 0.394, 122.0, "Тольятти"),
            DataDTO("B", 0.060, 186.0, "Самара"),
            DataDTO("C", 0.521, 67.0, "Чапаевск"),
            DataDTO("D", 0.109, 284.0, "Тольятти"),
            DataDTO("E", 0.159, 243.0, "Самара"),
            DataDTO("F", 0.794, 109.0, "Чапаевск"),
            DataDTO("G", 0.613, 82.0, "Тольятти"),
            DataDTO("H", 0.549, 110.0, "Самара"),
            DataDTO("I", 0.147, 41.0, "Чапаевск"),
            DataDTO("J", 0.136, 38.0, "Тольятти"),
            DataDTO("K", 0.135, 273.0, "Тольятти"),
            DataDTO("L", 0.926, 99.0, "Тольятти"),
            DataDTO("M", 0.358, 29.0, "Тольятти"),
            DataDTO("N", 0.888, 235.0, "Самара"),
            DataDTO("O", 0.019, 124.0, "Самара"),
            DataDTO("P", 0.071, 152.0, "Чапаевск"),
            DataDTO("Q", 0.016, 78.0, "Чапаевск"),
            DataDTO("R", 0.477, 35.0, "Самара"),
            DataDTO("S", 0.462, 14.0, "Тольятти"),
            DataDTO("T", 0.927, 172.0, "Тольятти"),
            DataDTO("U", 0.033, 59.0, "Чапаевск"),
            DataDTO("V", 0.345, 96.0, "Самара"),
            DataDTO("W", 0.850, 99.0, "Тольятти"),
            DataDTO("X", 0.576, 169.0, "Тольятти"),
            DataDTO("Y", 0.977, 161.0, "Самара"),
            DataDTO("Z", 0.626, 73.0, "Чапаевск"),
            DataDTO("AA",0.691, 202.0, "Тольятти"),
            DataDTO("AB",0.280, 25.0, "Тольятти"),
            DataDTO("AC",0.988, 257.0, "Тольятти"),
            DataDTO("AD",0.670, 261.0, "Тольятти"),
            DataDTO("AE",0.346, 199.0, "Тольятти"),
            DataDTO("AF",0.208, 247.0, "Тольятти"),
            DataDTO("AG",0.249, 214.0, "Чапаевск"),
            DataDTO("AH",0.364, 177.0, "Тольятти"),
            DataDTO("AI",0.480, 175.0, "Тольятти"),
            DataDTO("AJ",0.365, 263.0, "Самара"),
            DataDTO("AK",0.143, 224.0, "Самара"),
            DataDTO("AL",0.616, 27.0, "Тольятти"),
            DataDTO("AM",0.566, 12.0, "Тольятти"),
            DataDTO("AN",0.138, 277.0, "Тольятти"),
            DataDTO("AO",0.458, 79.0, "Тольятти"),
            DataDTO("AP",0.404, 250.0, "Тольятти"),
            DataDTO("AQ",0.266, 163.0, "Чапаевск"),
            DataDTO("AR",0.174, 244.0, "Чапаевск"),
            DataDTO("AS",0.474, 30.0, "Чапаевск"),
            DataDTO("AT",0.051, 160.0, "Тольятти"),
            DataDTO("AU",0.503, 201.0, "Тольятти"),
            DataDTO("AV",0.683, 290.0, "Тольятти"),
            DataDTO("AW",0.122, 164.0, "Чапаевск"),
            DataDTO("AX",0.956, 147.0, "Тольятти"),
            DataDTO("AY",0.171, 18.0, "Чапаевск"),
            DataDTO("AZ",0.795, 203.0, "Тольятти")
    )
}

private fun getNormalizeData(source: List<DataDTO>): List<DataDTO> {
    val result: MutableList<DataDTO> = mutableListOf()
    source.forEach { result.add(DataDTO(it.index, getNormalizeRatio(it.ratio), getNormalizeValue(it.value), it.city))}
    return result.toList()
}

fun processKmeans(isEuclidean: Boolean) {
    Random(2)
    val data = getNormalizeData(getSourceData())
    val startCentroids: MutableList<DataDTO> = mutableListOf()

    for (i in 0 until NUMBER_OF_CLUSTERS) {
        val centroid = data[Random.nextInt(data.size)]
        println("Центроид №${i+1}: " + centroid)
        startCentroids.add(centroid)
    }

    val table = HashMap<String, ParamDTO>()
    if (isEuclidean) {
        data.forEach { table[it.index] = getClusterNumberEuclidean(startCentroids, it) }
    } else {
        data.forEach { table[it.index] = getClusterNumberManhattan(startCentroids, it) }
    }

    val plotData = prepareDataForPlot(table, startCentroids)
    val p = lets_plot(plotData) {x = "x"; y = "y"; color = "cluster"} + geom_point(plotData) +
            geom_text(plotData, vjust = "top", hjust = 0) {label = "index"; }

    val pSpec = p.toSpec()
    val pSize = DoubleVector(600.0, 300.0)

    val component = MonolithicAwt.buildPlotFromRawSpecs(pSpec, pSize, SVG_COMPONENT_FACTORY_JFX, JFX_EDT_EXECUTOR) {}

    val start = JFrame("Первоначальное распределение по кластерам")
    start.contentPane.add(component)
    start.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    start.setLocationRelativeTo(null)
    start.pack()
    start.isVisible = true

    var E = getRatioE(table)
    println("Сумма квадратичных ошибок = $E\n")

    var iterations = 1

    do {
        println("\nИтерация №$iterations")

        E = getRatioE(table)

        val newCentroids: MutableList<DataDTO> = mutableListOf()
        for (i in 0 until NUMBER_OF_CLUSTERS) {
            val newRatio = getCentroidRatio(i, table, data)
            val newValue = getCentroidValue(i, table, data)
            val newCity = getCentroidCity(i, table, data)
            val c = DataDTO("C$i", newRatio, newValue, newCity)
            println("Центроид №${i+1}: " + c)
            newCentroids.add(c)
        }

        if (isEuclidean) {
            data.forEach { table[it.index] = getClusterNumberEuclidean(newCentroids, it) }
        } else {
            data.forEach { table[it.index] = getClusterNumberManhattan(newCentroids, it) }
        }

        val plotData = prepareDataForPlot(table, newCentroids)
        val p = lets_plot(plotData) {x = "x"; y = "y"; color = "cluster"} + geom_point(plotData) +
                geom_text(plotData, vjust = "top", hjust = 0) {label = "index"; }

        val plotSpec = p.toSpec()
        val plotSize = DoubleVector(600.0, 300.0)

        val component = MonolithicAwt.buildPlotFromRawSpecs(plotSpec, plotSize, SVG_COMPONENT_FACTORY_JFX, JFX_EDT_EXECUTOR) {}

        val frame = JFrame("Итерация №$iterations")
        frame.contentPane.add(component)
        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.setLocationRelativeTo(null)
        frame.pack()
        frame.isVisible = true

        val curE = getRatioE(table)
        println("Сумма квадратичных ошибок = $curE\n")
        iterations++
    } while (E - curE > 0.01)

    for (i in 0 until NUMBER_OF_CLUSTERS) {
        println("\nКластер №$i")
        table.filter { it.value.cluster == i }.forEach { print("${it.key} ") }
    }

}

private fun getCentroidRatio(index: Int, table: HashMap<String, ParamDTO>, data: List<DataDTO>): Double {
    var ratio = 0.0
    var count = 0

    table.filter { it.value.cluster == index }.forEach { entry ->
        ratio += data.find { it.index == entry.key }!!.ratio
        count++
    }
    return if (count != 0) ratio.div(count) else ratio
}

private fun getCentroidValue(index: Int, table: HashMap<String, ParamDTO>, data: List<DataDTO>): Double {
    var value = 0.0
    var count = 0

    table.filter { it.value.cluster == index }.forEach { entry ->
        value += data.find { it.index == entry.key }!!.value
        count++
    }
    return if (count != 0) value.div(count) else value
}

private fun getCentroidCity(index: Int, table: HashMap<String, ParamDTO>, data: List<DataDTO>): String {
    val tlt = "Тольятти"; var tltCount = 0
    table.filter { it.value.cluster == index }.forEach { entry ->
        if (data.find { it.index == entry.key }!!.city == tlt) tltCount++
    }
    val smr = "Самара"; var smrCount = 0
    table.filter { it.value.cluster == index }.forEach { entry ->
        if (data.find { it.index == entry.key }!!.city == smr) smrCount++
    }
    val chp = "Чапаевск"; var chpCount = 0
    table.filter { it.value.cluster == index }.forEach { entry ->
        if (data.find { it.index == entry.key }!!.city == chp) chpCount++
    }
    return when (maxOf(tltCount, smrCount, chpCount)) {
        tltCount -> tlt
        smrCount -> smr
        else -> chp
    }
}

private fun getClusterNumberEuclidean(centroids: MutableList<DataDTO>, node: DataDTO): ParamDTO {
    var minMetric = Double.MAX_VALUE
    var i = 0
    for ((index, value) in centroids.withIndex()) {
        val curMetric = getEuclideanMetric(value, node)
        if (curMetric < minMetric) {
            minMetric = curMetric
            i = index
        }
    }
    return ParamDTO(minMetric, i)
}

private fun getClusterNumberManhattan(centroids: MutableList<DataDTO>, node: DataDTO): ParamDTO {
    var minMetric = Double.MAX_VALUE
    var i = 0
    for ((index, value) in centroids.withIndex()) {
        val curMetric = getManhattanMetric(value, node)
        if (curMetric < minMetric) {
            minMetric = curMetric
            i = index
        }
    }
    return ParamDTO(minMetric, i)
}

private fun getRatioE(table: HashMap<String, ParamDTO>): Double {
    var E = 0.0
    table.forEach { E += it.value.metric.pow(2) }
    return E
}

private fun getEuclideanMetric(centroid: DataDTO, node: DataDTO): Double {
    val param1 = (centroid.ratio - node.ratio).pow(2.0)
    val param2 = (centroid.value - node.value).pow(2.0)
    val param3 = isSameTown(centroid, node)
    return sqrt(param1 + param2 + param3)
}

private fun getManhattanMetric(centroid: DataDTO, node: DataDTO): Double {
    val param1 = abs(centroid.ratio - node.ratio)
    val param2 = abs(centroid.value - node.value)
    val param3 = isSameTown(centroid, node)
    return param1 + param2 + param3
}

private fun isSameTown(centroid: DataDTO, node: DataDTO): Int {
    return if (centroid.city == node.city) 0 else 1
}

private fun getNormalizeRatio(ratio: Double): Double {
    return BigDecimal((ratio - 0.01).div(0.99)).setScale(4, RoundingMode.HALF_UP).toDouble()
}

fun getNormalizeValue(value: Double): Double {
    return BigDecimal((value - 1.0).div(299)).setScale(4, RoundingMode.HALF_UP).toDouble()
}

fun getDenormalizeRatio(ratio: Double): Double {
    return BigDecimal((ratio * 0.99).plus(0.01)).setScale(2, RoundingMode.HALF_UP).toDouble()
}

fun getDenormalizeValue(value: Double): Double {
    return BigDecimal((value * 299).plus(1)).setScale(0, RoundingMode.HALF_UP).toDouble()
}

fun prepareDataForPlot(table: HashMap<String, ParamDTO>, centroids: MutableList<DataDTO>): Map<String, List<Any>> {
    val source = getSourceData()
    val indexList: MutableList<String>   = mutableListOf()
    val ratioList: MutableList<Double>   = mutableListOf()
    val valueList: MutableList<Double>   = mutableListOf()
    val cityList: MutableList<String>    = mutableListOf()
    val clusterList: MutableList<String> = mutableListOf()

    table.forEach { entry ->
        indexList.add(entry.key)
        ratioList.add(source.find { it.index == entry.key }!!.ratio)
        valueList.add(source.find { it.index == entry.key }!!.value)
        cityList.add(source.find { it.index == entry.key }!!.city)
        clusterList.add(entry.value.cluster.toString())
    }

    for ((index, value) in centroids.withIndex()) {
        indexList.add(value.index)
        ratioList.add(getDenormalizeRatio(value.ratio))
        valueList.add(getDenormalizeValue(value.value))
        cityList.add(value.city)
        clusterList.add(index.toString())
    }

    return mapOf(
            "index" to indexList,
            "x" to valueList,
            "y" to ratioList,
            "city" to cityList,
            "cluster" to clusterList
    )
}

