Imports System.Data.SqlClient
Imports System.Data
Imports System.Security.Policy

Partial Class Login
   Inherits System.Web.UI.Page

   Private Function UserLogin(userName As String, password As String) As Boolean

      ' read the coonection string from web.config 
      Dim conString As String = ConfigurationManager.ConnectionStrings("VGO_Dev").ConnectionString

      Using con As New System.Data.SqlClient.SqlConnection(conString)
         '' declare the command that will be used to execute the select statement 
         Dim com As New SqlCommand("SELECT UserName FROM Admins WHERE UserName = @UserName AND Password = @Password", con)

         ' set the username and password parameters
         com.Parameters.Add("@UserName", SqlDbType.NVarChar).Value = userName
         com.Parameters.Add("@Password", SqlDbType.NVarChar).Value = password

         con.Open()
         '' execute the select statment 
         Dim result As String = Convert.ToString(com.ExecuteScalar())
         '' check the result 
         If String.IsNullOrEmpty(result) Then
            'invalid user/password , return flase 
            Return False
         Else
            ' valid login
            Return True
         End If
      End Using
   End Function

   Protected Sub Login1_Authenticate(sender As Object, e As AuthenticateEventArgs) Handles Login1.Authenticate
      Dim userName As String = Login1.UserName
      Dim password As String = Login1.Password

      Dim result As Boolean = UserLogin(userName, password)
      If (result) Then
            e.Authenticated = True
        Else
         e.Authenticated = False
      End If
   End Sub

    Protected Sub Login1_LoggedIn(sender As Object, e As EventArgs) Handles Login1.LoggedIn
        Response.Redirect("Auftrag_Overview_Web.aspx")
        If String.IsNullOrEmpty(Request.QueryString("ReturnUrl")) = True Then
            Response.Redirect("Auftrag_Overview_Web.aspx")
        Else
            Response.Redirect(Request.QueryString("ReturnUrl"))
        End If
    End Sub
End Class
