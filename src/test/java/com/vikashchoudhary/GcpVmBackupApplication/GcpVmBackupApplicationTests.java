// package com.vikashchoudhary.GcpVmBackupApplication;

// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
// class GcpVmBackupApplicationTests {

// 	@Test
// 	void contextLoads() {
// 	}

// }

package com.vikashchoudhary.GcpVmBackupApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GcpVmBackupApplicationTests {

   
    private int port = 8080;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
       
    }

    @Test
    void testListVMs() {
        
        String projectId = "orbital-choir-454209-p7"; 
        String zone = "asia-south1-a"; 

        String url = "http://localhost:" + port + "/gcp/list-vms";
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testCreateBackup() {
        
        String projectId = "orbital-choir-454209-p7";
        String zone = "asia-south1-a";
        String instanceName = "virtualmachine1"; 
        String snapshotName = "virtualmachine2"; 

        String url = "http://localhost:" + port + "/gcp/backup/create?projectId=" + projectId +
                "&zone=" + zone + "&instanceName=" + instanceName + "&snapshotName=" + snapshotName;
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Backup created successfully");
    }

    @Test
    void testListBackups() {
        
        String projectId = "orbital-choir-454209-p7"; 

        String url = "http://localhost:" + port + "/gcp/backup/list?projectId=" + projectId;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }
}