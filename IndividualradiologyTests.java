// Subclasses for Radiology Tests
class ChestXray extends RadiologyTest {
    public ChestXray() {
        super(
            "Chest X-Ray (PA View)",   
            "X-Ray",                   
            "Chest / Thorax",         
            "Posteroanterior (PA)",    
            350.00                     
        );
        this.contrastUsed = "No";
    }
}

class WholeAbdomenUltrasound extends RadiologyTest {
    public WholeAbdomenUltrasound() {
        super(
            "Whole Abdomen Ultrasound",     
            "Ultrasound",                   
            "Abdomen (Whole)",             
            "B-mode Transabdominal",       
            600.00                         
        );
        this.contrastUsed = "No";
    }
}

class KUBXray extends RadiologyTest {
    public KUBXray() {
        super(
            "KUB X-Ray",                    
            "X-Ray",                        
            "Kidneys / Ureters / Bladder",  
            "AP Supine",                    
            300.00                         
        );
        this.contrastUsed = "No";
    }
}