<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title>RCKMS eICR to vMR Converter Test Harness</title>
    <link rel="stylesheet" media="screen" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link href="/resources/css/main.css" rel="stylesheet"  type="text/css" />
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script
            src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
    <script>
        $(document)
            .ready(
                function() {
                    $('#addFile')
                        .click(
                            function() {
                                var fileIndex = $('#fileTable tr').children().length - 1;
                                $('#fileTable').append(
                                    '<tr><td>'
                                    + '   <input type="file" class="btn btn-default btn-file" name="file" />'
                                    + '</td></tr>');
                            }
                        );
                }
            );
    </script>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#"><spring:message code = "build.date" /></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <a class="navbar-brand" href="#">Changeset: <spring:message code = "revision" /></a>
            <a class="navbar-brand" href="#">Version No: <spring:message code = "version" /></a>
        </div><!--/.navbar-collapse -->
    </div>
</nav>

<div class="jumbotron">
    <div class="container">
        <h2>EICR to VMR Converter Test Harness</h2>
        <p><c:out value="${msg}" /></p>
        <form:form method="post" action="uploading" enctype="multipart/form-data">
        <p>Select files to upload to service. Press to Add button to add more file
            inputs.</p>

        <table id="fileTable">
            <tr>
                <span class="btn btn-default btn-file">
                <td><input name="file" type="file" /></td>
                </span>
            </tr>
        </table>
        <br />
        <button type="submit" class="btn btn-success">
            <i class="fa fa-upload"></i> Upload
        </button>
        <a class="btn btn-md btn-info" href="#" id="addFile"> <i class="fa fa-file-code-o "></i></i> Add File</a>
        </form:form>
        <div>
        </div>
</body>
</html>