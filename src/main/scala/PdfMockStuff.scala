import java.awt.Color

import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
object PdfMockStuff extends App {
  println("Hello John")

  import java.nio.file.Files

  val tmpFile = Files.createTempFile("MultiSigForm", ".pdf").toFile //In a Play setting, would want to use SingletonTemporaryFileCreator.create instead
  println(s"Created file $tmpFile")

  val doc = new PDDocument()
  try {
    val postscriptFont = PDType1Font.HELVETICA_BOLD
    //val trueTypeFont = PDType0Font.load(doc, some-TTF-file)
    val fontSize = 12

    val page1 = new PDPage()
    val page1Content = new PDPageContentStream(doc, page1)

    def appendImage(fullPathToImage: String, x: Float, y: Float, scale: Float,page: PDPage) = {
      val img = PDImageXObject.createFromFile(fullPathToImage, doc)
      val contentStream = new PDPageContentStream(doc, page, AppendMode.PREPEND, true, true)
      contentStream.drawImage(img, x, y, img.getWidth() * scale, img.getHeight() * scale)
      contentStream.close()
    }

   //first side of page

    appendImage("/home/colm/Applications/hmrc-development-environment/hmrc/pdfstuff/public/images/hmrc-logo.png", 100, 700, 0.2f,page1)

    drawText(100,655,6,"Please fill in the whole form using a ball point pen and send it to")
    makeRectangleWithBorder(100,200,100,550)
    page1Content.setFont(postscriptFont,10)

   //address
    drawText(100,640,10,"HMRC Direct Debit Support Team")
    drawText(100,630,10,"VAT 2")
    drawText(100,620,10,"DMB 612")
    drawText(100,610,10,"BX5 5AB")


    drawText(100,540,6,"Name(s) of account holder(s)")
    makeRectangleWithBorder(20,200,100,510)
    makeRectangleWithBorder(20,200,100,490)

    drawText(100,480,6,"Bank/building society account number")
    makeABunchOfBoxesInALine(20,20,85,455,8)

    drawText(100,435,6,"Branch sort code")
    makeABunchOfBoxesInALine(20,20,85,410,6)

    drawText(100,400,6, "Name and full postal address of your bank or building society")

    page1Content.setFont(postscriptFont,10)
    makeRectangleWithBorder(20,200,100,370)
    makeRectangleWithBorder(20,200,100,350)
    makeRectangleWithBorder(20,200,100,330)
    makeRectangleWithBorder(20,200,100,310)
    //todo why could I not put the draw text inbetween them
    //todo also change color
    drawText(110 ,385,3, "To the manager")
    drawText(260 ,385,3, "Bank/building society")
    drawText(110 ,365,3, "Address")
    drawText(230 ,325,3, "Postcode")

    //Second side of page
    appendImage("/home/colm/Applications/hmrc-development-environment/hmrc/pdfstuff/public/images/dd-logo.jpg", 400, 700, 0.1f,page1)

    drawText(330 ,665,12, "Instruction to your")
    drawText(330 ,655,12, "bank or building society")
    drawText(330 ,645,12, "to pay by Direct Debit")

    drawText(330 ,560,6, "Service user number")
    makeABunchOfBoxesInALine(20,10,315,530,6)
    drawText(330 ,500,6, "Reference")
    makeABunchOfBoxesInALine(20,10,315,470,19)


    drawText(330 ,450,3, "Instruction to your bank or building society")
    drawText(330 ,440,3, "Please pay HMRC E VAT DDSfrom the account detailed in this")
    drawText(330 ,430,3, "Instruction subject to the safeguards assured by the Direct Debit")
    drawText(330 ,420,3, "Guarantee.I understand that this Instruction may remain with HMRC E")
    drawText(330 ,410,3, "VAT DDS and, if so, details will be passed electronically to my")
    drawText(330 ,400,3, "bank/building society")

    makeRectangleWithBorder(20,200,330,350)
    makeRectangleWithBorder(20,200,330,330)
    makeRectangleWithBorder(20,200,330,320)

    drawText(330 ,365,3, "Signature(s)")
    drawText(330 ,335,3, "Date")


    drawText(200 ,280,6, "Banks and building societies may not accept Direct Debit Instructions for some types of account")


    def drawText(posX: Int ,posY:Int, font:Int, text:String) :Unit = {
      page1Content.setNonStrokingColor(Color.BLACK)
      page1Content.setFont(postscriptFont,font)
      page1Content.beginText()
      page1Content.newLineAtOffset(posX, posY)
      page1Content.showText(text)
      page1Content.endText()
    }

    def makeABunchOfBoxesInALine(height:Int,width:Int,startPosX: Int , startPosY:Int,howManyBoxes:Int):Unit={

      for(x <- 1 to howManyBoxes){
        makeRectangleWithBorder(height, width ,startPosX + (x * width),startPosY )
      }
    }

    def makeRectangleWithBorder(height:Int,width:Int,posX: Int , posY:Int) :Unit = {
      page1Content.setNonStrokingColor(Color.darkGray)
      page1Content.addRect(posX  - 1, posY -1, width + 3, height + 3)
      page1Content.fill()
      page1Content.setNonStrokingColor(Color.white)
      page1Content.addRect(posX, posY, width, height)
      page1Content.fill()
    }

    page1Content.close()

    doc.addPage(page1)
    //Page 2

    val page2 = new PDPage()
    val page2Content = new PDPageContentStream(doc, page2)
    appendImage("/home/colm/Applications/hmrc-development-environment/hmrc/pdfstuff/public/images/dd-guarantee.png", 100, 300, 0.5f,page2)
    page2Content.close()

    doc.addPage(page2)

    println(s"Saving to file $tmpFile...")
    doc.save(tmpFile)
    println(s"Saved")
  } finally {
    doc.close()
  }

  java.awt.Desktop.getDesktop.open(tmpFile)
}
