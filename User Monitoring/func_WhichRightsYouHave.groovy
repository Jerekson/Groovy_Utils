whichRights = { priv -> 
    myrights = []
    priv -= 14 // Base
    while(priv > 0){
        if(priv >= 2048){
            myrights.add("Public Access enabled")
			priv -= 2048
        }else if(priv >= 256){
            myrights.add("System administration rights")
			priv -= 256
        }else if(priv >= 64){
            myrights.add("Can create/modify groups")
			priv -= 64
        }else if(priv >= 32){
            myrights.add("Can create/modify users")
			priv -= 32
        }else if(priv >= 16){
            myrights.add("User administration rights")
			priv -= 16
        }else if(priv >= 1){
            myrights.add("Log-in enabled")
			priv -= 1
        }else{
            myrights.add("Unkwown")
        }
    }
    return myrights
}


out << whichRights(2415 as int)