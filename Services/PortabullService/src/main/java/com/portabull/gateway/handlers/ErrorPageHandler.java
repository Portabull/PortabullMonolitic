package com.portabull.gateway.handlers;

import com.portabull.gateway.config.GatewayConfiguration;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ErrorPageHandler extends ValveBase {

    private boolean showReport = true;

    private boolean showServerInfo = true;

    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();

    public ErrorPageHandler() {
        super(true);
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        this.getNext().invoke(request, response);
        if (response.isCommitted()) {
            if (response.setErrorReported()) {
                try {
                    response.flushBuffer();
                } catch (Throwable var5) {
                    ExceptionUtils.handleThrowable(var5);
                }

                response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("javax.servlet.error.exception"));
            }

        } else {
            Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
            if (!request.isAsync() || request.isAsyncCompleting()) {
                if (throwable != null && !response.isError()) {
                    response.reset();
                    response.sendError(500);
                }

                response.setSuspended(false);

                try {
                    this.report(request, response);
                } catch (Throwable var6) {
                    ExceptionUtils.handleThrowable(var6);
                }

            }
        }
    }

    protected void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode >= 400 && response.getContentWritten() <= 0L && response.setErrorReported()) {
            AtomicBoolean result = new AtomicBoolean(false);
            response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
            if (result.get()) {
                ErrorPage errorPage = null;
                if (throwable != null) {
                    errorPage = this.errorPageSupport.find(throwable);
                }

                if (errorPage == null) {
                    errorPage = this.errorPageSupport.find(statusCode);
                }

                if (errorPage == null) {
                    errorPage = this.errorPageSupport.find(0);
                }

                if (errorPage == null || !this.sendErrorPage(errorPage.getLocation(), response)) {
                    String message = Escape.htmlElementContent(response.getMessage());
                    String reason;
                    if (message == null) {
                        if (throwable != null) {
                            reason = throwable.getMessage();
                            if (reason != null && reason.length() > 0) {
                                Scanner scanner = new Scanner(reason);
                                Throwable var10 = null;

                                try {
                                    message = Escape.htmlElementContent(scanner.nextLine());
                                } catch (Throwable var25) {
                                    var10 = var25;
                                    throw var25;
                                } finally {
                                    if (scanner != null) {
                                        if (var10 != null) {
                                            try {
                                                scanner.close();
                                            } catch (Throwable var23) {
                                                var10.addSuppressed(var23);
                                            }
                                        } else {
                                            scanner.close();
                                        }
                                    }

                                }
                            }
                        }

                        if (message == null) {
                            message = "";
                        }
                    }

                    reason = null;
                    String description = null;
                    StringManager smClient = StringManager.getManager("org.apache.catalina.valves", request.getLocales());
                    response.setLocale(smClient.getLocale());

                    try {
                        reason = smClient.getString("http." + statusCode + ".reason");
                        description = smClient.getString("http." + statusCode + ".desc");
                    } catch (Throwable var24) {
                        ExceptionUtils.handleThrowable(var24);
                    }

                    if (reason == null || description == null) {
                        if (message.isEmpty()) {
                            return;
                        }

                        reason = smClient.getString("errorReportValve.unknownReason");
                        description = smClient.getString("errorReportValve.noDescription");
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("<!doctype html><html lang=\"");
                    sb.append(smClient.getLocale().getLanguage()).append("\">");
                    sb.append("<head>");
                    sb.append("<title>");
                    sb.append(smClient.getString("errorReportValve.statusHeader", new Object[]{String.valueOf(statusCode), reason}));
                    sb.append("</title>");
                    sb.append("<style type=\"text/css\">");
                    sb.append("body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}");
                    sb.append("</style>");
                    sb.append("</head><body>");
                    sb.append("<h1>");
                    sb.append(smClient.getString("errorReportValve.statusHeader", new Object[]{String.valueOf(statusCode), reason})).append("</h1>");
                    if (this.isShowReport()) {
                        sb.append("<hr class=\"line\" />");
                        sb.append("<p><b>");
                        sb.append(smClient.getString("errorReportValve.type"));
                        sb.append("</b> ");
                        if (throwable != null) {
                            sb.append(smClient.getString("errorReportValve.exceptionReport"));
                        } else {
                            sb.append(smClient.getString("errorReportValve.statusReport"));
                        }

                        sb.append("</p>");
                        if (!message.isEmpty()) {
                            sb.append("<p><b>");
                            sb.append(smClient.getString("errorReportValve.message"));
                            sb.append("</b> ");
                            sb.append(message).append("</p>");
                        }

                        sb.append("<p><b>");
                        sb.append(smClient.getString("errorReportValve.description"));
                        sb.append("</b> ");
                        sb.append(description);
                        sb.append("</p>");
                        if (throwable != null) {
                            String stackTrace = this.getPartialServletStackTrace(throwable);
                            sb.append("<p><b>");
                            sb.append(smClient.getString("errorReportValve.exception"));
                            sb.append("</b></p><pre>");
                            sb.append(Escape.htmlElementContent(stackTrace));
                            sb.append("</pre>");
                            int loops = 0;

                            for (Throwable rootCause = throwable.getCause(); rootCause != null && loops < 10; ++loops) {
                                stackTrace = this.getPartialServletStackTrace(rootCause);
                                sb.append("<p><b>");
                                sb.append(smClient.getString("errorReportValve.rootCause"));
                                sb.append("</b></p><pre>");
                                sb.append(Escape.htmlElementContent(stackTrace));
                                sb.append("</pre>");
                                rootCause = rootCause.getCause();
                            }

                            sb.append("<p><b>");
                            sb.append(smClient.getString("errorReportValve.note"));
                            sb.append("</b> ");
                            sb.append(smClient.getString("errorReportValve.rootCauseInLogs"));
                            sb.append("</p>");
                        }

                        sb.append("<hr class=\"line\" />");
                    }

                    if (this.isShowServerInfo()) {
                        sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
                    }

                    sb.append("</body></html>");

                    try {
                        try {
                            response.setContentType("text/html");
                            response.setCharacterEncoding("utf-8");
                        } catch (Throwable var27) {
                            ExceptionUtils.handleThrowable(var27);
                            if (this.container.getLogger().isDebugEnabled()) {
                                this.container.getLogger().debug("status.setContentType", var27);
                            }
                        }

                        Writer writer = response.getReporter();
                        if (writer != null) {
                            writer.write(sb.toString());
                            response.finishResponse();
                        }
                    } catch (IOException var28) {
                    } catch (IllegalStateException var29) {
                    }

                }
            }
        }
    }

    protected void report(Request request, Response response) {

        //TODO:
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<script>");
        sb.append("init();");
        sb.append("function init()");
        sb.append("{");

        if (request.getRequestURL() == null || !request.getRequestURL().toString().contains("APIGateway")) {
            sb.append("window.location.href = \"" + GatewayConfiguration.getBaseUrl() + "/portabull\";");
        } else {
            sb.append("window.location.href = \"" + GatewayConfiguration.getBaseUrl() + "/pagenotfound.html\";");
        }


        sb.append("}");
        sb.append("</script>");
        sb.append("</body>");
        sb.append("</html>");


        try {
            try {
                response.setContentType("text/html");
                response.setCharacterEncoding("utf-8");
            } catch (Throwable var27) {
                ExceptionUtils.handleThrowable(var27);
                if (this.container.getLogger().isDebugEnabled()) {
                    this.container.getLogger().debug("status.setContentType", var27);
                }
            }

            Writer writer = response.getReporter();
            if (writer != null) {
                writer.write(sb.toString());
                response.finishResponse();
            }
        } catch (IOException var28) {
        } catch (IllegalStateException var29) {
        }

    }

    protected String getPartialServletStackTrace(Throwable t) {
        StringBuilder trace = new StringBuilder();
        trace.append(t.toString()).append(System.lineSeparator());
        StackTraceElement[] elements = t.getStackTrace();
        int pos = elements.length;

        int i;
        for (i = elements.length - 1; i >= 0; --i) {
            if (elements[i].getClassName().startsWith("org.apache.catalina.core.ApplicationFilterChain") && elements[i].getMethodName().equals("internalDoFilter")) {
                pos = i;
                break;
            }
        }

        for (i = 0; i < pos; ++i) {
            if (!elements[i].getClassName().startsWith("org.apache.catalina.core.")) {
                trace.append('\t').append(elements[i].toString()).append(System.lineSeparator());
            }
        }

        return trace.toString();
    }

    private boolean sendErrorPage(String location, Response response) {
        File file = new File(location);
        if (!file.isAbsolute()) {
            file = new File(this.getContainer().getCatalinaBase(), location);
        }

        if (file.isFile() && file.canRead()) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            try {
                OutputStream os = response.getOutputStream();
                Throwable var5 = null;

                try {
                    InputStream is = new FileInputStream(file);
                    Throwable var7 = null;

                    try {
                        IOTools.flow(is, os);
                    } catch (Throwable var32) {
                        var7 = var32;
                        throw var32;
                    } finally {
                        if (is != null) {
                            if (var7 != null) {
                                try {
                                    is.close();
                                } catch (Throwable var31) {
                                    var7.addSuppressed(var31);
                                }
                            } else {
                                is.close();
                            }
                        }

                    }
                } catch (Throwable var34) {
                    var5 = var34;
                    throw var34;
                } finally {
                    if (os != null) {
                        if (var5 != null) {
                            try {
                                os.close();
                            } catch (Throwable var30) {
                                var5.addSuppressed(var30);
                            }
                        } else {
                            os.close();
                        }
                    }

                }

                return true;
            } catch (IOException var36) {
                this.getContainer().getLogger().warn(sm.getString("errorReportValve.errorPageIOException", new Object[]{location}), var36);
                return false;
            }
        } else {
            this.getContainer().getLogger().warn(sm.getString("errorReportValve.errorPageNotFound", new Object[]{location}));
            return false;
        }
    }

    public void setShowReport(boolean showReport) {
        this.showReport = showReport;
    }

    public boolean isShowReport() {
        return this.showReport;
    }

    public void setShowServerInfo(boolean showServerInfo) {
        this.showServerInfo = showServerInfo;
    }

    public boolean isShowServerInfo() {
        return this.showServerInfo;
    }

    public boolean setProperty(String name, String value) {
        ErrorPage ep;
        if (name.startsWith("errorCode.")) {
            int code = Integer.parseInt(name.substring(10));
            ep = new ErrorPage();
            ep.setErrorCode(code);
            ep.setLocation(value);
            this.errorPageSupport.add(ep);
            return true;
        } else if (name.startsWith("exceptionType.")) {
            String className = name.substring(14);
            ep = new ErrorPage();
            ep.setExceptionType(className);
            ep.setLocation(value);
            this.errorPageSupport.add(ep);
            return true;
        } else {
            return false;
        }
    }

    public String getProperty(String name) {
        String result;
        ErrorPage ep;
        if (name.startsWith("errorCode.")) {
            int code = Integer.parseInt(name.substring(10));
            ep = this.errorPageSupport.find(code);
            if (ep == null) {
                result = null;
            } else {
                result = ep.getLocation();
            }
        } else if (name.startsWith("exceptionType.")) {
            String className = name.substring(14);
            ep = this.errorPageSupport.find(className);
            if (ep == null) {
                result = null;
            } else {
                result = ep.getLocation();
            }
        } else {
            result = null;
        }

        return result;
    }

}

