package com.sq.lib

import org.dom4j.io.SAXReader
import java.io.File

object MyClass {
    @JvmStatic
    fun main(args: Array<String>) {
        var fileName = File(System.getProperty("user.dir"),"/lib/AndroidManifest.xml")
//        fileName = this.javaClass.classLoader.getResource("./AndroidManifest.xml")
        println(fileName)
        val document = SAXReader().read(fileName)
        document.rootElement.elements().first().elements()[1].element("intent-filter").element("action").attribute("name").value = "xiaoy.MAINxxx"
        val root = document.rootElement.elements().first().elements()[1].element("intent-filter").element("action").attribute("name").value

        println(root)
//        val target = root.element("manifest").element("application")
//        val value  = target.attributeValue("android:name")
//        print(value)
    }
}