package com.base.auth.config;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.AccountForTokenDto;
import com.base.auth.dto.UserForTokenDto;
import com.base.auth.model.Permission;
import com.base.auth.utils.ZipUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Qualifier("primaryJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public CustomTokenEnhancer(@Qualifier("primaryJdbcTemplate") JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        String username = authentication.getName();
        String grantType = authentication.getOAuth2Request().getRequestParameters().get("grantType");

        if(authentication.getOAuth2Request().getGrantType() != null){
            if(grantType != null){
                String email = authentication.getOAuth2Request().getRequestParameters().get("email");
                additionalInfo = getAdditionalInfoByEmail(email, authentication.getOAuth2Request().getGrantType());
            } else {
                String email = authentication.getOAuth2Request().getRequestParameters().get("email");
                additionalInfo = getAdditionalInfoByEmail(email, authentication.getOAuth2Request().getGrantType());
            }
        }else{
            if(grantType.equals(SecurityConstant.GRANT_TYPE_USER) ){
                String phone = authentication.getOAuth2Request().getRequestParameters().get("phone");
                additionalInfo = getAdditionalUserInfo(phone, grantType);
            }
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

    private Map<String, Object> getAdditionalUserInfo(String phone, String grantType) {
        Map<String, Object> additionalInfo = new HashMap<>();
        AccountForTokenDto a = getUserByPhone(phone);

        if (a != null) {
            Long userId = a.getId();
            Long storeId = -1L;
            String kind = a.getKind() +  "";//token kind
            Long deviceId = -1L;// id cua thiet bi, lưu ở table device để get firebase url..
            String pemission = "<>";//empty string
            Integer userKind = a.getKind(); //loại user là admin hay là gì
            Integer tabletKind = -1;
            Long orderId = -1L;
            Boolean isSuperAdmin = a.getIsSuperAdmin();
            String tenantId = "";
            additionalInfo.put("user_id", a.getId());
            additionalInfo.put("user_kind", a.getKind());
            additionalInfo.put("grant_type", grantType == null ? SecurityConstant.GRANT_TYPE_PASSWORD : grantType);
            additionalInfo.put("tenant_info", tenantId);
            String DELIM = "|";
            String additionalInfoStr = ZipUtils.zipString(userId + DELIM
                    + storeId + DELIM
                    + kind + DELIM
                    + pemission + DELIM
                    + deviceId + DELIM
                    + userKind + DELIM
                    + phone + DELIM
                    + tabletKind + DELIM
                    + orderId + DELIM
                    + isSuperAdmin + DELIM
                    + tenantId);
            additionalInfo.put("additional_info", additionalInfoStr);
        }
        return additionalInfo;
    }

    private Map<String, Object> getAdditionalInfo(String username, String grantType) {
        Map<String, Object> additionalInfo = new HashMap<>();
        AccountForTokenDto a = getAccountByUsername(username);

        if (a != null) {
            Long accountId = a.getId();
            Long storeId = -1L;
            String kind = a.getKind() + "";//token kind
            Long deviceId = -1L;// id cua thiet bi, lưu ở table device để get firebase url..
            String pemission = "<>";//empty string
            Integer userKind = a.getKind(); //loại user là admin hay là gì
            Integer tabletKind = -1;
            Long orderId = -1L;
            Boolean isSuperAdmin = a.getIsSuperAdmin();
            String tenantId = "";
            additionalInfo.put("user_id", accountId);
            additionalInfo.put("user_kind", a.getKind());
            additionalInfo.put("grant_type", grantType);
            additionalInfo.put("tenant_info", tenantId);
            String DELIM = "|";
            String additionalInfoStr = ZipUtils.zipString(accountId + DELIM
                    + storeId + DELIM
                    + kind + DELIM
                    + pemission + DELIM
                    + deviceId + DELIM
                    + userKind + DELIM
                    + username + DELIM
                    + tabletKind + DELIM
                    + orderId + DELIM
                    + isSuperAdmin + DELIM
                    + tenantId);
            additionalInfo.put("additional_info", additionalInfoStr);
        }
        return additionalInfo;
    }

    private Map<String, Object> getAdditionalInfoByEmail(String email, String grantType) {
        Map<String, Object> additionalInfo = new HashMap<>();
        AccountForTokenDto a = getUserByEmail(email);

        if (a != null) {
            Long userId = a.getId();
            Long storeId = -1L;
            String kind = a.getKind() + ""; // token kind
            Long deviceId = -1L; // id của thiết bị, lưu ở table device để lấy firebase url
            String permission = "<>"; // empty string
            Integer userKind = a.getKind(); // loại user là admin hay gì
            Integer tabletKind = -1;
            Long orderId = -1L;
            Boolean isSuperAdmin = a.getIsSuperAdmin();
            String tenantId = "";

            additionalInfo.put("user_id", userId);
            additionalInfo.put("user_kind", userKind);
            additionalInfo.put("grant_type", grantType == null ? SecurityConstant.GRANT_TYPE_EMAIL : grantType);
            additionalInfo.put("tenant_info", tenantId);

            String DELIM = "|";
            String additionalInfoStr = ZipUtils.zipString(userId + DELIM
                    + storeId + DELIM
                    + kind + DELIM
                    + permission + DELIM
                    + deviceId + DELIM
                    + userKind + DELIM
                    + email + DELIM // sử dụng email thay vì username hoặc phone
                    + tabletKind + DELIM
                    + orderId + DELIM
                    + isSuperAdmin + DELIM
                    + tenantId);

            additionalInfo.put("additional_info", additionalInfoStr);
        }
        return additionalInfo;
    }

    public AccountForTokenDto getAccountByUsername(String username) {
        try {
            String query = "SELECT id, kind, username, email, full_name, is_super_admin " +
                    "FROM db_user_base_account WHERE username = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{username},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (dto.size() > 0)return dto.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountForTokenDto getUserByPhone(String phone) {
        try {
            String query = "SELECT id, kind, username, email, full_name, is_super_admin " +
                    "FROM db_user_base_account WHERE phone = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{phone},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (dto.size() > 0)return dto.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountForTokenDto getUserByEmail(String email) {
        try {
            String query = "SELECT id, kind, username, email, full_name, is_super_admin " +
                    "FROM db_user_base_account WHERE email = ? and status = 1 limit 1";
            log.debug(query);
            List<AccountForTokenDto> dto = jdbcTemplate.query(query, new Object[]{email},  new BeanPropertyRowMapper<>(AccountForTokenDto.class));
            if (dto.size() > 0)return dto.get(0);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
