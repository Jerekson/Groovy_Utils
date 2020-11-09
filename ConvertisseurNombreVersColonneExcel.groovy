
def convertisseur_numero_colonne = { 
    
}

/* equivalence VBA

Public Function ColID2Ltr(columnId As Integer) As String

 

    If columnId > 26 Then

        ' si la colonne est supérieur a 26, il faut la représenter sur au moins 2 caractères

        ' on récupère la partie la plus a droite du nombre a décomposer, et on appelle de façon récursive ColID2Ltr

        Dim partieGauche As Integer, partieDroite As Integer

        If (columnId Mod 26 0) Then

            partieDroite = columnId Mod 26

            partieGauche = (columnId - partieDroite) / 26

        Else

            partieDroite = 26

            partieGauche = (columnId / 26) - 1

        End If

        ColID2Ltr = ColID2Ltr(partieGauche) + NumToChar(partieDroite)

    Else

        ColID2Ltr = NumToChar(columnId)

    End If

 

End Function

 

Public Function NumToChar(id As Integer) As String

 

    NumToChar = Chr(id + 64)

 

End Function

*/
