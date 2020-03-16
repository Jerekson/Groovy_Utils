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

whichRightsYouHave = { priv ->
    priv -= 14 // First least-significant bits aren’t used
    
    def allRights = [
        1:      'Log-in enabled',
//      2:      '[reserved]',
//      4:      '[reserved]',
//      8:      '[reserved]',
        16:     'User administration rights',
        32:     'Can create/modify users',
        64:     'Can create/modify groups',
        256:    'System administration rights',
        2048:   'Public Access enabled',

        128:    '[unknown permission (128)]',
        512:    '[unknown permission (512)]',
        1024:   '[unknown permission (1024)]',
        4096:   '[unknown permission (4096)]',
    ]
    def myRights = []

    allRights.each { permNum, permLabel ->
        if (priv & permNum) {
            // Current bit of permNum matches one in allRights
            myRights << permLabel
        }
    }
    return myRights
}


out << whichRights(2415 as int)