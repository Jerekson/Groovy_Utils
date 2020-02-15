whichRights = { priv -> 
    def myrights = []
    out << """priv -> ${priv}<br>"""
    priv -= 14
    out << """priv -> ${priv}<br>"""
    priv = 3
    while(priv > 0){
        out << "while start <br>"
        switch(priv){
            case priv >= 2048:
                out << "less"
                break;
            default:
                out << "default"
                priv -= 1
                break;
        }
        /*
		switch(priv){
			case priv >= 2048:
				//myrights.add("Public Access enabled")
				priv -= 2048
                out << """priv -> ${priv}<br>"""
				break;
			case priv >= 256:
				//myrights.add("System administration rights")
				priv -= 256
                out << """priv -> ${priv}<br>"""
				break;
			case priv >= 64:
				//myrights.add("Can create/modify groups")
				priv -= 64
                out << """priv -> ${priv}<br>"""
				break;
			case priv >= 32:
				//myrights.add("Can create/modify users")
				priv -= 32
                out << """priv -> ${priv}<br>"""
				break;
			case priv >= 16:
				//myrights.add("User administration rights")
				priv -= 16
                out << """priv -> ${priv}<br>"""
				break;
			case priv >= 1:
				//myrights.add("Log-in enabled")
				priv -= 1
                out << """priv -> ${priv}<br>"""
				break;
		}
        */
        out << priv
        out << "<br>"
        
    }
    return myrights
}


whichRights(2399 as int)
//result = whichRights(2399 as int)
/*
result.each{
    out << it
    out << "<br>"
}*/