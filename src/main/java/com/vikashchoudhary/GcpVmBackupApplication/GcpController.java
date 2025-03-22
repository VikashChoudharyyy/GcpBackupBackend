package com.vikashchoudhary.GcpVmBackupApplication;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.ListInstancesRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/gcp")
@CrossOrigin(origins = "http://localhost:3000")
public class GcpController {
    
    @GetMapping("/list-vms")
    public List<String> listVMs() throws IOException {
        List<String> vmNames = new ArrayList<>();
        String projectId = "orbital-choir-454209-p7";
        String zone = "asia-south1-a"; 

        
        try (InstancesClient instancesClient = InstancesClient.create()) {
            ListInstancesRequest request = ListInstancesRequest.newBuilder()
                    .setProject(projectId)
                    .setZone(zone)
                    .build();
                    String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                    System.out.println("Using credentials file: " + credentialsPath);

            
            for (Instance instance : instancesClient.list(request).iterateAll()) {
                vmNames.add(instance.getName());
            }
        }

        return vmNames;
    }
}
