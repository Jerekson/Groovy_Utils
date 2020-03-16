SELECT 
	*
FROM 
	DAuditNew
WHERE
	ValueKey = 'UserPrivs'
	--UserID = 146011
	ORDER BY AuditDate DESC;