package br.com.a3sitsolutions.tempo_box.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "file_descriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDescription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "repository")
    private String repository;
    
    @Column(name = "commit_hash")
    private String commit;
    
    @Column(name = "commit_message", columnDefinition = "TEXT")
    private String commitMessage;
    
    @Column(name = "branch")
    private String branch;
    
    @Column(name = "run_id")
    private String runId;
    
    @Column(name = "actor")
    private String actor;
    
    @Column(name = "apk_name")
    private String apkName;
    
    @Column(name = "download_url")
    private String downloadUrl;
}