package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.StudentController;
import org.example.entities.Student;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentControllerTest {

    @Autowired
    private StudentController studentController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void createStudent() throws Exception {
        Student student = new Student();
        student.setId(1);
        student.setName("Filippo");
        student.setSurname("Rossi");
        student.setIsWorking(true);

        String studentJSON = objectMapper.writeValueAsString(student);

        MvcResult resultActions = this.mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }
    @Test
    @Order(2)
    void contextLoads() {
        assertThat(studentController).isNotNull();
    }
    @Test
    @Order(3)
    void updateStudentById() throws Exception {
        Integer studentId = 1;
        Student updatedStudent = new Student(studentId, "Updated", "Name", false);
        String studentJSON = objectMapper.writeValueAsString(updatedStudent);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/students/{id}/update", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertNotNull(content);
    }

    @Test
    @Order(4)
    void readStudentList() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/students/all"))
                .andDo(print()).andReturn();

        List<Student> userFromResponseList = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(userFromResponseList.size()).isNotZero();


    }

    @Test
    @Order(5)
    void getStudentById() throws Exception {
        Integer studentId = 1;
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/students/{id}/research", studentId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(studentId))
                .andReturn();
    }

    @Test
    @Order(6)
    void deleteStudentById() throws Exception {
        Integer studentId = 1;
        MvcResult result = mockMvc.perform(delete("/students/{id}/delete", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

}