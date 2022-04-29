<%@ page import="resource.server.SearchEngineServer" %> <%@ page language="java"
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@ page import="java.io.*, java.util.*, java.text.*, jakarta.servlet.*,
jakarta.servlet.http.*, org.rocksdb.*, org.rocksdb.util.*"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252" />
  </head>

  <body>
    The results are: <br />
    <% SearchEngineServer.doGet(request, response); %>
  </body>
</html>
