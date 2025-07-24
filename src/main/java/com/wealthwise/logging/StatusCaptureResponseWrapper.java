package com.wealthwise.logging;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class StatusCaptureResponseWrapper extends HttpServletResponseWrapper {
    private int httpStatus = SC_OK;

    public StatusCaptureResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    @Override
    public void sendError(int sc) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    @Override
    public void sendError(int sc, String msg) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    public int getStatus() {
        return this.httpStatus;
    }
}
