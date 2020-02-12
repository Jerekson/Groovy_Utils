import java.time.*
import java.time.format.DateTimeFormatter

isLeapYear = {tyear -> 
    if(((tyear % 4 == 0) && (tyear % 100 != 0)) || tyear % 400 == 0){
        return true
    }else{
        return false
    }
}

getPreviousMonth = {int tmonth -> 
    if(tmonth == 1){
        pmonth = 12
    }else{
        pmonth -= 1
    }
    return pmonth as String
}

getPreviousDay = {int tday, tmonth, tyear -> 
    switch(tday){
        case 1:
        
        break;
        
    }
    if(tday == 1){
        //Vérifier quel est le mois précédent (si c'est 28 ou 29 ou 30 ou 31)
    }else{
        tday -= 1
    }
    return tday as String
}


getPreviousDate = { tdate -> 
    def (month, day, year) = tdate.split("/")
    
    pday = getPreviousDay(day)
    
    
    return 'yes' as String
}

LocalDate ld = LocalDate.parse(LocalDate.now() as String, DateTimeFormatter.ISO_LOCAL_DATE)
todayDate = ld.format(DateTimeFormatter.ofPattern('MM/dd/yyyy'))

String previousDate = getPreviousDate(todayDate)