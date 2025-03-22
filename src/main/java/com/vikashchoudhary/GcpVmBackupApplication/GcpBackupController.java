package com.vikashchoudhary.GcpVmBackupApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.google.cloud.compute.v1.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 
@RestController
@RequestMapping("/gcp/backup")
@CrossOrigin(origins = "http://localhost:3000")
public class GcpBackupController {

    
    @PostMapping("/create")
    public String createBackup(
            @RequestParam String projectId,
            @RequestParam String zone,
            @RequestParam String instanceName,
            @RequestParam String snapshotName) throws IOException {
        try (InstancesClient instancesClient = InstancesClient.create();
             SnapshotsClient snapshotsClient = SnapshotsClient.create()) {

            Instance instance = instancesClient.get(projectId, zone, instanceName);
            String diskName = instance.getDisksList().get(0).getSource();

            Snapshot snapshot = Snapshot.newBuilder()
                    .setName(snapshotName)
                    .setSourceDisk(diskName)
                    .build();

            InsertSnapshotRequest request = InsertSnapshotRequest.newBuilder()
                    .setProject(projectId)
                    .setSnapshotResource(snapshot)
                    .build();

            snapshotsClient.insertAsync(request).get();

            return "Backup created successfully: " + snapshotName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating backup: " + e.getMessage();
        }
    }

    @GetMapping("/list")
    public List<String> listBackups(@RequestParam String projectId) throws IOException {
        List<String> snapshotNames = new ArrayList<>();

        try (SnapshotsClient snapshotsClient = SnapshotsClient.create()) {
            for (Snapshot snapshot : snapshotsClient.list(projectId).iterateAll()) {
                snapshotNames.add(snapshot.getName());
            }
        }

        return snapshotNames;
    }

    @PostMapping("/restore")
    public String restoreVm(
            @RequestParam String projectId,
            @RequestParam String zone,
            @RequestParam String instanceName,
            @RequestParam String snapshotName,
            @RequestParam String newInstanceName) throws IOException {
        try (DisksClient disksClient = DisksClient.create();
             InstancesClient instancesClient = InstancesClient.create()) {

            Disk restoredDisk = Disk.newBuilder()
                    .setName(newInstanceName + "-disk")
                    .setSourceSnapshot(String.format("projects/%s/global/snapshots/%s", projectId, snapshotName))
                    .build();

            InsertDiskRequest diskRequest = InsertDiskRequest.newBuilder()
                    .setProject(projectId)
                    .setZone(zone)
                    .setDiskResource(restoredDisk)
                    .build();

            disksClient.insertAsync(diskRequest).get();

            AttachedDisk attachedDisk = AttachedDisk.newBuilder()
                    .setBoot(true)
                    .setSource(String.format("projects/%s/zones/%s/disks/%s", projectId, zone, restoredDisk.getName()))
                    .build();

            NetworkInterface networkInterface = NetworkInterface.newBuilder()
                    .setName("global/networks/default")
                    .build();

            Instance newInstance = Instance.newBuilder()
                    .setName(newInstanceName)
                    .addDisks(attachedDisk)
                    .addNetworkInterfaces(networkInterface)
                    .setMachineType(String.format("zones/%s/machineTypes/n1-standard-1", zone))
                    .build();

            InsertInstanceRequest instanceRequest = InsertInstanceRequest.newBuilder()
                    .setProject(projectId)
                    .setZone(zone)
                    .setInstanceResource(newInstance)
                    .build();

            instancesClient.insertAsync(instanceRequest).get();

            return "VM restored successfully: " + newInstanceName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error restoring VM: " + e.getMessage();
        }
    }
}