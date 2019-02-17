package com.bjjc.scmapp

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testString() {
        var str = formatInOrganizationText(
            "一汽-大众备件中转库（成都）," +
                    "一汽-大众备件中转库（成都）," +
                    "一汽-大众备件中转库（成都）," +
                    "一汽-大众备件中转库（成都）," +
                    "一汽-大众备件中转库（成都）"
        )
    }

    private fun formatInOrganizationText(str: String): String {
        val strs: ArrayList<String> = str.split(",") as ArrayList<String>
        val strBuilder: StringBuilder = java.lang.StringBuilder()
        for (value in strs) {
            strBuilder.append(value)
            strBuilder.append("\n")
            strBuilder.append(",")
        }
        return strBuilder.toString()
    }

    //Tests sorting and multithreading
    @Test
    fun testSorting(){
        val nums = intArrayOf(234,345,444,3344,1,50,16,4567,8654)
        for(value in nums){
            Thread{
                Thread.sleep(value.toLong())
                println(value)
            }.start()
        }
        Thread.sleep(10000)
//        val sc = Scanner(System.`in`)
//        val s1 = sc.nextLine()
//        print(s1)

    }
}
