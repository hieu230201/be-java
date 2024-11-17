package org.example.basewebsub.restapis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.example.basewebsub.comon.ErrorCode;
import org.example.basewebsub.comon.SubConstants;
import org.example.basewebsub.exception.CustomServiceBusinessException;
import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.bases.ILogManage;
import org.example.basewebsub.logging.common.MessageLog;
import org.example.basewebsub.restapis.model.ApiModel;
import org.example.basewebsub.restapis.model.AuthorizationModel;
import org.example.basewebsub.util.JsonConvertUtil;
import org.example.basewebsub.util.StringBuilderPlus;
import org.example.basewebsub.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hieunt
 */
public class ApiUtils {
    public static class PlusEncoderInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            return execution.execute(new HttpRequestWrapper(request) {
                @Override
                public URI getURI() {
                    URI u = super.getURI();
                    String strictlyEscapedQuery = StringUtils.replace(u.getRawQuery(), "+", "%2B");
                    return UriComponentsBuilder.fromUri(u).replaceQuery(strictlyEscapedQuery).build(true).toUri();
                }
            }, body);
        }
    }

    private static final ILogManage logManage = LogManage.getLogManage(ApiUtils.class);

    protected String baseUri;

    // Determines the timeout in milliseconds until a connection is established.
    protected static final int CONNECT_TIMEOUT = 60000;
    protected static final int READ_TIMEOUT = 300000;

    protected HttpHeaders headers;

    protected HttpHeaders getHeaders(MediaType mediaType, Map<String, String> headerParams,
                                     AuthorizationModel authModel) {
        try {
            headers = null;
            if (mediaType == null)
                mediaType = MediaType.APPLICATION_JSON;
            List<MediaType> lstMediaType = new ArrayList<>();
            lstMediaType.add(mediaType);

            headers = new HttpHeaders();
            headers.setAccept(lstMediaType);
            headers.setContentType(mediaType);
            // set header param
            if (!headerParams.isEmpty() && headerParams.size() > 0) {
                for (Map.Entry<String, String> entry : headerParams.entrySet())     {
                    headers.set(entry.getKey(), entry.getValue());
                }
            }
            // set Authorization
            String token = null;
            if (authModel != null && authModel.getIsAuthenToken())
                token = authModel.getToken();
            else
                token = authorization(authModel);
            headers.set("Authorization", token);
        } catch (Exception e) {
            return headers;
        }
        return headers;
    }

    protected String authorization(AuthorizationModel authModel) {
        String authHeader = null;
        try {
            if (authModel != null) {
                String auth = authModel.getUser() + ":" + authModel.getPassword();
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charsets.UTF_8));
                authHeader = "Basic " + new String(encodedAuth);
            }
        } catch (Exception e) {
            return null;
        }
        return authHeader;
    }

    protected boolean setBaseUri(String baseUri) {
        try {
            new URL(baseUri);
            return true;
        } catch (Exception ignore) {
            return false;
        }

    }

    protected RestTemplate getRestTemplate() {
        var restTemplate = new RestTemplate();
        if (StringUtil.isNotNullAndEmpty(baseUri)) {
            restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUri));
        }
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        restTemplate.setRequestFactory(requestFactory);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new PlusEncoderInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    protected RestTemplate getRestTemplateSSL() {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
                socketFactoryRegistry);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                .setConnectionManager(connectionManager).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        if (StringUtil.isNotNullAndEmpty(baseUri)) {
            restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUri));
        }
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new PlusEncoderInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    protected <T> T makeRequestCall(HttpMethod httpMethod, String url, Map<String, String> headerParams,
                                    AuthorizationModel authModel, Class<T> responseType, Map<String, ?> params, HttpEntity<?> entity)
            throws CustomServiceBusinessException {
        long startTime = System.currentTimeMillis();
        Map<String, String> messageMap = new HashMap<>();
        String clientMessageId = null;
        try {
            if (headerParams != null) {
                if (headerParams.containsKey(SubConstants.CLIENT_MESSAGE_ID))
                    clientMessageId = headerParams.get(SubConstants.CLIENT_MESSAGE_ID);

            }
            logRequest(httpMethod, url, headerParams, params, entity);

            RestTemplate restTemplate = getRestTemplate();

            ObjectMapper mapper = new ObjectMapper();
            if (entity != null && entity.getHeaders() != null) {
                if (MediaType.APPLICATION_FORM_URLENCODED.equalsTypeAndSubtype(entity.getHeaders().getContentType()))
                    restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));
            }
            ResponseEntity<T> response;
            if (params == null) {
                response = restTemplate.exchange(url, httpMethod, entity, responseType);
            } else {
                response = restTemplate.exchange(url, httpMethod, entity, responseType, params);
            }
            logResponse(url, headerParams, response, startTime);

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            messageMap.put(SubConstants.BODY, e.getResponseBodyAsString());
            messageMap.put(SubConstants.PARAMS, JsonConvertUtil.convertObjectToJson(entity));
            messageMap.put(SubConstants.PATH, url);

            MessageLog messageLog = new MessageLog(clientMessageId, this.getClass().getName(), httpMethod.name(),
                    params, messageMap, null);
            messageLog.setPath(url);
            messageLog.setDuration(System.currentTimeMillis() - startTime);

            logManage.error(messageLog);
            throw new CustomServiceBusinessException(formatError(e.getResponseBodyAsString()), ErrorCode.CALL_API_HTTP_ERROR);
        } catch (ResourceAccessException ex) {
            messageMap.put("message", String.format("End call api: %s  RequestId: %s totalTime: %s with Exception %s",
                    url, clientMessageId, String.valueOf(System.currentTimeMillis() - startTime), ex.getMessage()));
            MessageLog messageLog = new MessageLog(clientMessageId, this.getClass().getName(), httpMethod.name(),
                    params, messageMap, ex);

            messageLog.setPath(url);
            messageLog.setDuration(System.currentTimeMillis() - startTime);
            logManage.error(messageLog);
            throw new CustomServiceBusinessException(ex.getMessage(), ErrorCode.CALL_API_ERROR);
        }
    }

    protected <T> T makeRequestCallSSL(HttpMethod httpMethod, String url, Map<String, String> headerParams,
                                       AuthorizationModel authModel, Class<T> responseType, Map<String, ?> params, HttpEntity<?> entity)
            throws CustomServiceBusinessException {
        long startTime = System.currentTimeMillis();
        Map<String, String> messageMap = new HashMap<>();
        String clientMessageId = null;
        try {
            if (headerParams != null) {
                if (headerParams.containsKey(SubConstants.CLIENT_MESSAGE_ID))
                    clientMessageId = headerParams.get(SubConstants.CLIENT_MESSAGE_ID);

            }

            logRequest(httpMethod, url, headerParams, params, entity);

            RestTemplate restTemplate = getRestTemplateSSL();

            ObjectMapper mapper = new ObjectMapper();
            if (entity != null && entity.getHeaders() != null) {
                if (MediaType.APPLICATION_FORM_URLENCODED.equalsTypeAndSubtype(entity.getHeaders().getContentType()))
                    restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));
            }
            ResponseEntity<T> response;
            if (params == null) {
                response = restTemplate.exchange(url, httpMethod, entity, responseType);
            } else {
                response = restTemplate.exchange(url, httpMethod, entity, responseType, params);
            }

            logResponse(url, headerParams, response, startTime);

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            messageMap.put(SubConstants.BODY, e.getResponseBodyAsString());
            messageMap.put(SubConstants.PARAMS, JsonConvertUtil.convertObjectToJson(entity));
            messageMap.put(SubConstants.PATH, url);

            MessageLog messageLog = new MessageLog(clientMessageId, this.getClass().getName(), httpMethod.name(),
                    params, messageMap, null);
            messageLog.setPath(url);
            messageLog.setDuration(System.currentTimeMillis() - startTime);

            logManage.error(messageLog);
            throw new CustomServiceBusinessException(formatError(e.getResponseBodyAsString()), ErrorCode.CALL_API_HTTP_ERROR);
        } catch (ResourceAccessException ex) {
            messageMap.put("message", String.format("End call api: %s  RequestId: %s totalTime: %s with Exception %s",
                    url, clientMessageId, String.valueOf(System.currentTimeMillis() - startTime), ex.getMessage()));
            MessageLog messageLog = new MessageLog(clientMessageId, this.getClass().getName(), httpMethod.name(),
                    params, messageMap, ex);

            messageLog.setPath(url);
            messageLog.setDuration(System.currentTimeMillis() - startTime);
            logManage.error(messageLog);
            throw new CustomServiceBusinessException(ex.getMessage(), ErrorCode.CALL_API_ERROR);
        }
    }

    // call post - put - delete với data instanceof Object
    public <T> T makeRequestCall(HttpMethod httpMethod, String url, MediaType mediaType,
                                 Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                 Map<String, ?> params, ObjectNode body) throws CustomServiceBusinessException {
        HttpEntity<ObjectNode> entity = new HttpEntity<>(body, getHeaders(mediaType, headerParams, authModel));

        return makeRequestCall(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }

    public <T> T makeRequestCall(HttpMethod httpMethod, String url, MediaType mediaType,
                                 Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                 Map<String, ?> params, String uriVariables) throws CustomServiceBusinessException {
        HttpEntity<String> entity = new HttpEntity<String>(uriVariables, getHeaders(mediaType, headerParams, authModel));

        return makeRequestCall(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }

    public <T> T makeRequestCall(HttpMethod httpMethod, String url, MediaType mediaType,
                                 Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                 Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        HttpEntity<ApiModel> entity = new HttpEntity<>(body, getHeaders(mediaType, headerParams, authModel));
        return makeRequestCall(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }


    // call post - put - delete với data instanceof ApiModel
    public <T> T makeRequestCallSSL(HttpMethod httpMethod, String url, MediaType mediaType,
                                    Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                    Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        HttpEntity<ApiModel> entity = new HttpEntity<>(body, getHeaders(mediaType, headerParams, authModel));
        return makeRequestCallSSL(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }

    // call get
    public <T> T makeRequestCall(HttpMethod httpMethod, String url, MediaType mediaType,
                                 Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                 Map<String, ?> params) throws CustomServiceBusinessException {
        HttpEntity<?> entity = new HttpEntity<>(null, getHeaders(mediaType, headerParams, authModel));
        return makeRequestCall(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }

    // call get
    public <T> T makeRequestCallSSL(HttpMethod httpMethod, String url, MediaType mediaType,
                                    Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                                    Map<String, ?> params) throws CustomServiceBusinessException {
        HttpEntity<?> entity = new HttpEntity<>(null, getHeaders(mediaType, headerParams, authModel));
        return makeRequestCallSSL(httpMethod, url, headerParams, authModel, responseType, params, entity);
    }

    /**
     * @param url
     * @param headerParams map các tham số header
     * @param authModel    token hoặc user/pass
     * @param responseType
     * @param params
     * @return
     * @throws CustomServiceBusinessException
     */
    public <T> T get(String url, Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                     Map<String, ?> params) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.GET, url, MediaType.APPLICATION_JSON, headerParams, authModel, responseType,
                params);
    }

    public <T> T getSSL(String url, Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType,
                        Map<String, ?> params) throws CustomServiceBusinessException {
        return makeRequestCallSSL(HttpMethod.GET, url, MediaType.APPLICATION_JSON, headerParams, authModel, responseType,
                params);
    }

    public <T> T get(String url, Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType)
            throws CustomServiceBusinessException {
        return get(url, headerParams, authModel, responseType, null);
    }

    public <T> T getSSL(String url, Map<String, String> headerParams, AuthorizationModel authModel, Class<T> responseType)
            throws CustomServiceBusinessException {
        return getSSL(url, headerParams, authModel, responseType, null);
    }

    public <T> T post(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                      Class<T> responseType, Map<String, ?> params, ObjectNode body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.POST, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T post(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                      Class<T> responseType, Map<String, ?> params, String jsArray) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.POST, url, mediaType, headerParams, authModel, responseType, params, jsArray);
    }

    public <T> T post(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                      Class<T> responseType, Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.POST, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T postSSL(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                         Class<T> responseType, Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        return makeRequestCallSSL(HttpMethod.POST, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T put(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                     Class<T> responseType, Map<String, ?> params, ObjectNode body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.PUT, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T put(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                     Class<T> responseType, Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.PUT, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T patch(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                       Class<T> responseType, Map<String, ?> params, ObjectNode body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.PATCH, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T patch(String url, MediaType mediaType, Map<String, String> headerParams, AuthorizationModel authModel,
                       Class<T> responseType, Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.PATCH, url, mediaType, headerParams, authModel, responseType, params, body);
    }

    public <T> T delete(String url, Map<String, String> headerParams, AuthorizationModel authModel,
                        Class<T> responseType, Map<String, ?> params, ObjectNode body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.DELETE, url, MediaType.APPLICATION_JSON, headerParams, authModel,
                responseType, params, body);
    }

    public <T> T delete(String url, Map<String, String> headerParams, AuthorizationModel authModel,
                        Class<T> responseType, Map<String, ?> params, ApiModel body) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.DELETE, url, MediaType.APPLICATION_JSON, headerParams, authModel,
                responseType, params, body);
    }

    public <T> T delete(String url, Map<String, String> headerParams, AuthorizationModel authModel,
                        Class<T> responseType, Map<String, ?> params) throws CustomServiceBusinessException {
        return makeRequestCall(HttpMethod.DELETE, url, MediaType.APPLICATION_JSON, headerParams, authModel,
                responseType, params);
    }

    protected String formatError(String response) {
        try {
            StringBuilderPlus message = new StringBuilderPlus();
            JSONObject objJson = new JSONObject(response);
            message.append("Trạng thái: ");
            message.appendNewLine(String.format("%s", objJson.getInt("status")));
            message.appendNewLine(objJson.getString("error"));
            message.appendNewLine(objJson.getString("soaErrorDesc"));
            //message.appendNewLine("RequestId: " + objJson.getString(SubConstants.CLIENT_MESSAGE_ID));

            return message.toString();
        } catch (JSONException e) {
            return response;
        }
    }

    protected void logRequest(HttpMethod httpMethod, String url, Map<String, String> headerParams, Map<String, ?> params,
                              HttpEntity<?> entity) {
        String clientMessageId = null;
        try {
            if (headerParams != null && (headerParams.containsKey(SubConstants.CLIENT_MESSAGE_ID)))
                clientMessageId = headerParams.get(SubConstants.CLIENT_MESSAGE_ID);
            MessageLog messageLog = getMessageLog(clientMessageId, httpMethod.name() + "|ApiUtils - logRequest");
            messageLog.setParameters(params);

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put(SubConstants.PATH, url);
            messageMap.put(SubConstants.CLIENT_MESSAGE_ID, clientMessageId);
            if (entity != null) {
                messageMap.put(SubConstants.HEADERS, JsonConvertUtil.convertObjectToJson(entity.getHeaders()));
                messageMap.put(SubConstants.BODY, JsonConvertUtil.convertObjectToJson(entity.getBody()));
            }
            messageMap.put(SubConstants.PARAMS, JsonConvertUtil.convertObjectToJson(params));

            messageLog.setMessages(messageMap);
            messageLog.setPath(url);
            logManage.info(messageLog);
        } catch (Exception e) {
            logManage.error(String.format("logRequest Exception:id=%s,fullstack=%s", clientMessageId, e.toString()), e);
        }
    }

    protected <T> void logResponse(String uri, Map<String, String> headerParams, ResponseEntity<T> response,
                                   Long startTime) {
        String clientMessageId = null;
        try {
            if (headerParams != null && (headerParams.containsKey(SubConstants.CLIENT_MESSAGE_ID)))
                clientMessageId = headerParams.get(SubConstants.CLIENT_MESSAGE_ID);
            MessageLog messageLog = getMessageLog(clientMessageId, "ApiUtils - logResponse");
            if (startTime != null)
                messageLog.setDuration(System.currentTimeMillis() - startTime);
            messageLog.setParameters(headerParams);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put(SubConstants.PATH, uri);
            messageMap.put(SubConstants.CLIENT_MESSAGE_ID, clientMessageId);
            if (response != null) {
                messageMap.put(SubConstants.STATUS_CODE, response.getStatusCode());
                messageMap.put(SubConstants.HEADERS, JsonConvertUtil.convertObjectToJson(response.getHeaders()));
                messageMap.put(SubConstants.BODY, JsonConvertUtil.convertObjectToJson(response.getBody()));
            }

            messageLog.setMessages(messageMap);
//            if (httpServletRequest != null)
            messageLog.setPath(uri);
            logManage.info(messageLog);
        } catch (Exception e) {
            logManage.error(String.format("logResponse Exception:id=%s,fullstack=%s", clientMessageId, e.toString()),
                    e);
        }
    }

    protected MessageLog getMessageLog(String clientMessageId, String methodName) {
        MessageLog messageLog = new MessageLog();
        messageLog.setClassName(this.getClass().getName());
        messageLog.setMethodName(methodName);
        messageLog.setId(clientMessageId);
        return messageLog;
    }


}