package com.m2d2.base.engines;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public abstract class Engine {

    private AsyncHttpClient _client;
    private static boolean _isAuthenticating;
    private List<ServiceRequest> _pendingRequestQueue;

    protected final static int HTTP_GET = 0;
    protected final static int HTTP_POST = 1;
    protected final static int HTTP_PUT = 2;
    protected final static int HTTP_DELETE = 3;

    public Engine() {
        _client = new AsyncHttpClient();
        _isAuthenticating = false;
        _pendingRequestQueue = new ArrayList<ServiceRequest>();
        setAuthValues();
    }


    /* IMPLEMENT THESE METHODS IN YOUR ABSTRACT APP ENGINE
     *
     * Tip: Use 'final' keyword to implement these abstract methods
     * Example: final protected void setAuthValues()
     *
     */

    protected abstract String getBaseURL();
    protected abstract void setAuthValues();
    protected abstract boolean checkAuthStatus();
    protected abstract void authenticate(boolean force);


    /* Each child command indicates whether a valid auth session is required for callService to work
     * Good for public API calls / auth calls
     */

    protected abstract boolean authenticationRequired();


    /* callService should be called by all child engines to perform network IO */

    protected void callService(int op, String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (checkAuthStatus()) {
            switch(op) {
                case HTTP_GET:
                    _client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
                    break;
                case HTTP_POST:
                    _client.post(getAbsoluteUrl(relativeUrl), params, responseHandler);
                    break;
                case HTTP_PUT:
                    _client.put(getAbsoluteUrl(relativeUrl), params, responseHandler);
                    break;
                case HTTP_DELETE:
                    _client.delete(getAbsoluteUrl(relativeUrl), responseHandler);
                    break;
            }
        } else {
            authenticate(false);
            _pendingRequestQueue.add(new ServiceRequest(op, relativeUrl, params, responseHandler));
        }
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return (relativeUrl == null) ? getBaseURL() : getBaseURL() + relativeUrl;
    }

    protected AsyncHttpClient getClient() {
        return _client;
    }


    /* Authentication State: prevents multiple auth calls */

    protected void beginAuthenticating() {
        _isAuthenticating = true;
    }

    protected void finishAuthenticating() {
        _isAuthenticating = false;
    }

    protected boolean isAuthenticating() {
        return _isAuthenticating;
    }


    protected void resumePendingQueueOperations() {
        Log.d("M2D2::Engine", "Session refreshed. Resuming pending requests (" + _pendingRequestQueue.size() + ")...");
        for (ServiceRequest req : _pendingRequestQueue) {
            callService(req._op, req._relativeUrl, req._params, req._responseHandler);
        }
        _pendingRequestQueue.clear();
    }


    /* Caching callService calls */

    class ServiceRequest {
        public int _op;
        public String _relativeUrl;
        public RequestParams _params;
        public AsyncHttpResponseHandler _responseHandler;

        public ServiceRequest(int op, String relativeUrl, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            _op = op;
            _relativeUrl = relativeUrl;
            _params = params;
            _responseHandler = responseHandler;
        }
    }
}
