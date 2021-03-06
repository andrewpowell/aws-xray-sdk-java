package com.amazonaws.xray.strategy.sampling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.xray.entities.SearchPattern;

public class SamplingRule {
    private static final Log logger = LogFactory.getLog(SamplingRule.class);

    private String serviceName;
    private String httpMethod;
    private String urlPath;
    private int fixedTarget = -1;
    private float rate = -1.0f;

    private Reservoir reservoir;

    public SamplingRule() {
        this.reservoir = new Reservoir();
    }

    /**
     * Constructs a new {@code SamplingRule}. Patterns are supported in the {@code serviceName}, {@code httpMethod}, and {@code urlPath} parameters. Patterns are matched using the {@link com.amazonaws.xray.entities.SearchPattern} class.
     *
     * @param serviceName
     *            the service name for which the rule should apply
     * @param httpMethod
     *            the http method for which the rule should apply
     * @param urlPath
     *            the urlPath for which the rule should apply
     * @param fixedTarget
     *            the number of traces per any given second for which the sampling rule will sample
     * @param rate
     *            the rate at which the rule should sample, after the fixedTarget has been reached
     */
    public SamplingRule(String serviceName, String httpMethod, String urlPath, int fixedTarget, float rate) {
        this.serviceName = serviceName;
        this.httpMethod = httpMethod;
        this.urlPath = urlPath;
        this.fixedTarget = fixedTarget;
        this.rate = rate;

        this.reservoir = new Reservoir(fixedTarget);
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the httpMethod
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod
     *            the httpMethod to set
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * @return the urlPath
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     *            the urlPath to set
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return the fixedTarget
     */
    public int getFixedTarget() {
        return fixedTarget;
    }

    /**
     * @param fixedTarget
     *            the fixedTarget to set
     */
    public void setFixedTarget(int fixedTarget) {
        this.fixedTarget = fixedTarget;
        this.reservoir = new Reservoir(fixedTarget);
    }

    /**
     * @return the rate
     */
    public float getRate() {
        return rate;
    }

    /**
     * @param rate
     *            the rate to set
     */
    public void setRate(float rate) {
        this.rate = rate;
    }

    /**
     * @return the reservoir
     */
    public Reservoir getReservoir() {
        return reservoir;
    }

    @Override
    public String toString() {
        return "\n\tservice_name: " + serviceName + "\n\thttp_method: " + httpMethod + "\n\turl_path: " + urlPath + "\n\tfixed_target: " + fixedTarget + "\n\trate: " + rate;
    }

    /**
     * Determines whether or not this sampling rule applies to the incoming request based on some of the request's parameters. Any null parameters provided will be considered an implicit match. For example, {@code appliesTo(null, null, null)} will always return {@code true}, for any rule.
     *
     * @param requestServiceName
     *            the service name for the incoming request. In current middleware implementations, the host header is used as the service name.
     * @param requestPath
     *            the path from the incoming request
     * @param requestMethod
     *            the method used to make the incoming request
     * @return whether or not this rule applies to the incoming request
     */
    public boolean appliesTo(String requestServiceName, String requestPath, String requestMethod) {
        return (null == serviceName || SearchPattern.wildcardMatch(serviceName, requestServiceName)) &&
            (null == requestPath || SearchPattern.wildcardMatch(urlPath, requestPath)) &&
            (null == requestMethod || SearchPattern.wildcardMatch(httpMethod, requestMethod));
    }

}
