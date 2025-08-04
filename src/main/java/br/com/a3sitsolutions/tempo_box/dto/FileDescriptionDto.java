package br.com.a3sitsolutions.tempo_box.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDescriptionDto {
    private String repository;
    private String commit;
    private String commitMessage;
    private String branch;
    private String runId;
    private String actor;
    private String apkName;
    private String downloadUrl;
}