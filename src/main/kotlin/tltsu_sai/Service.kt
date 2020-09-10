import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Service {


    companion object {
        private const val NUMBER_OF_CLUSTERS = 3

        fun getSourceData(): List<DataDTO> {
            return listOf(
                    DataDTO("A", 0.394, 122, "Тольятти"),
                    DataDTO("B", 0.060, 186, "Самара"),
                    DataDTO("C", 0.521, 67, "Чапаевск"),
                    DataDTO("D", 0.109, 284, "Тольятти"),
                    DataDTO("E", 0.159, 243, "Самара"),
                    DataDTO("F", 0.794, 109, "Чапаевск"),
                    DataDTO("G", 0.613, 82, "Тольятти"),
                    DataDTO("H", 0.549, 110, "Самара"),
                    DataDTO("I", 0.147, 41, "Чапаевск"),
                    DataDTO("J", 0.136, 38, "Тольятти"),
                    DataDTO("K", 0.135, 273, "Тольятти"),
                    DataDTO("L", 0.926, 99, "Тольятти"),
                    DataDTO("M", 0.358, 29, "Тольятти"),
                    DataDTO("N", 0.888, 235, "Самара"),
                    DataDTO("O", 0.019, 124, "Самара"),
                    DataDTO("P", 0.071, 152, "Чапаевск"),
                    DataDTO("Q", 0.016, 78, "Чапаевск"),
                    DataDTO("R", 0.477, 35, "Самара"),
                    DataDTO("S", 0.462, 14, "Тольятти"),
                    DataDTO("T", 0.927, 172, "Тольятти"),
                    DataDTO("U", 0.033, 59, "Чапаевск"),
                    DataDTO("V", 0.345, 96, "Самара"),
                    DataDTO("W", 0.850, 99, "Тольятти"),
                    DataDTO("X", 0.576, 169, "Тольятти"),
                    DataDTO("Y", 0.977, 161, "Самара"),
                    DataDTO("Z", 0.626, 73, "Чапаевск"),
                    DataDTO("AA",0.691, 202, "Тольятти"),
                    DataDTO("AB",0.280, 25, "Тольятти"),
                    DataDTO("AC",0.988, 257, "Тольятти"),
                    DataDTO("AD",0.670, 261, "Тольятти"),
                    DataDTO("AE",0.346, 199, "Тольятти"),
                    DataDTO("AF",0.208, 247, "Тольятти"),
                    DataDTO("AG",0.249, 214, "Чапаевск"),
                    DataDTO("AH",0.364, 177, "Тольятти"),
                    DataDTO("AI",0.480, 175, "Тольятти"),
                    DataDTO("AJ",0.365, 263, "Самара"),
                    DataDTO("AK",0.143, 224, "Самара"),
                    DataDTO("AL",0.616, 27, "Тольятти"),
                    DataDTO("AM",0.566, 12, "Тольятти"),
                    DataDTO("AN",0.138, 277, "Тольятти"),
                    DataDTO("AO",0.458, 79, "Тольятти"),
                    DataDTO("AP",0.404, 250, "Тольятти"),
                    DataDTO("AQ",0.266, 163, "Чапаевск"),
                    DataDTO("AR",0.174, 244, "Чапаевск"),
                    DataDTO("AS",0.474, 30, "Чапаевск"),
                    DataDTO("AT",0.051, 160, "Тольятти"),
                    DataDTO("AU",0.503, 201, "Тольятти"),
                    DataDTO("AV",0.683, 290, "Тольятти"),
                    DataDTO("AW",0.122, 164, "Чапаевск"),
                    DataDTO("AX",0.956, 147, "Тольятти"),
                    DataDTO("AY",0.171, 18, "Чапаевск"),
                    DataDTO("AZ",0.795, 203, "Тольятти")
            )
        }

        private fun getNormalizeData(source: List<DataDTO>): List<DataDTO> {
            val result: MutableList<DataDTO> = mutableListOf()
            return result
        }

        fun processKmeans(isEuclidean: Boolean) {
            Random(2)
            val data = getSourceData()
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

            println("Первоначальное распределение по кластерам")
            table.forEach { println("Запись ${it.key} - кластер ${it.value.cluster}") }

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
                    val c = DataDTO("c$i", newRatio, newValue, newCity)
                    println("Центроид №${i+1}: " + c)
                    newCentroids.add(c)
                }

                if (isEuclidean) {
                    data.forEach { table[it.index] = getClusterNumberEuclidean(newCentroids, it) }
                } else {
                    data.forEach { table[it.index] = getClusterNumberManhattan(newCentroids, it) }
                }
                table.forEach { println("Запись ${it.key} - кластер ${it.value.cluster}") }

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

        private fun getCentroidValue(index: Int, table: HashMap<String, ParamDTO>, data: List<DataDTO>): Int {
            var value = 0
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
            val param2 = (centroid.value - node.value).toDouble().pow(2.0)
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

        fun getNormalizeValue(value: Int): Double {
            return BigDecimal((value - 1.0).div(299)).setScale(4, RoundingMode.HALF_UP).toDouble()
        }

        fun getDenormalizeRatio(ratio: Double): Double {
            return BigDecimal((ratio * 0.99).plus(0.01)).setScale(2, RoundingMode.HALF_UP).toDouble()
        }

        fun getDenormalizeValue(value: Double): Int {
            val res = BigDecimal((value * 299).plus(1)).setScale(2, RoundingMode.HALF_UP).toDouble()
            return if (res.minus(res.toInt()) >= 0.5) res.toInt() + 1 else res.toInt()
        }

        fun prepareDataForPlot(table: HashMap<String, ParamDTO>): Map<String, List<Any>> {
            val source = Service.getSourceData()
            val indexList: MutableList<String> = mutableListOf()
            val ratioList: MutableList<Double> = mutableListOf()
            val valueList: MutableList<Int>    = mutableListOf()
            val cityList: MutableList<String>  = mutableListOf()
            val clusterList: MutableList<String>  = mutableListOf()

            table.forEach { entry ->
                indexList.add(entry.key)
                ratioList.add(source.find { it.index == entry.key }!!.ratio)
                valueList.add(source.find { it.index == entry.key }!!.value)
                cityList.add(source.find { it.index == entry.key }!!.city)
                clusterList.add(entry.value.cluster.toString())
            }

            return mapOf(
                    "index" to indexList,
                    "x" to valueList,
                    "y" to ratioList,
                    "city" to cityList,
                    "cluster" to clusterList
            )
        }
    }

}
