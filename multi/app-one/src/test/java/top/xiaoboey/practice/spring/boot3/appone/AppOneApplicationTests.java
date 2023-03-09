package top.xiaoboey.practice.spring.boot3.appone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleLog;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleLogService;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppOneApplicationTests {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleLogService simpleLogService;

    @Test
    void userHello(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/user/hello"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Hello, world!\"}"));

        mvc.perform(MockMvcRequestBuilders.get("/user/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "abc"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Hello, abc!\"}"));
    }

    /**
     * acquire a bearer token via login
     *
     * @param mvc
     * @param name
     * @param pwd
     * @return
     * @throws Exception
     */
    String acquireBearerToken(MockMvc mvc, String name, String pwd) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> map = new HashMap<>(1);
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", name)
                        .param("pwd", pwd))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    ApiResult<String> apiResult = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    if (apiResult.getCode() == HttpServletResponse.SC_OK) {
                        map.put("token", apiResult.getContent());
                    }
                });

        String token = (String) map.getOrDefault("token", null);
        assertThat(token).isNotEmpty();
        return "Bearer " + token;
    }

    @Test
    void adminBusinessTest(@Autowired MockMvc mvc) throws Exception {
        String adminToken = acquireBearerToken(mvc, "admin", "admin");

        String urlTemplate = "/business/someInfo";
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Some Info Found null!\"}"));

        mvc.perform(MockMvcRequestBuilders.get(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("key", "abc"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Some Info Found abc!\"}"));

        mvc.perform(MockMvcRequestBuilders.get("/business/dig")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().is(HttpServletResponse.SC_FORBIDDEN));
    }

    @Test
    void workerBusinessTest(@Autowired MockMvc mvc) throws Exception {
        String workerToken = acquireBearerToken(mvc, "worker", "worker");

        mvc.perform(MockMvcRequestBuilders.get("/business/someInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, workerToken))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Some Info Found null!\"}"));

        mvc.perform(MockMvcRequestBuilders.get("/business/sensitiveInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, workerToken))
                .andExpect(status().is(HttpServletResponse.SC_FORBIDDEN));

        mvc.perform(MockMvcRequestBuilders.get("/business/dig")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, workerToken))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"code\":200,\"content\":\"Digging\"}"));
    }

    @Test
    void accessLogTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:MM:ss"));
        int limit = 50;
        List<SimpleLog> logList = simpleLogService.listRecentLogs(limit);
        for (SimpleLog log: logList) {
            logger.info("{}", objectMapper.writeValueAsString(log));
        }
    }
}
