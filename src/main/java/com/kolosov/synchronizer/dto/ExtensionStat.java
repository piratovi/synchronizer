package com.kolosov.synchronizer.dto;

import com.kolosov.synchronizer.domain.FileSync;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExtensionStat {
    public String name;
    public int count;
    public List<FileSync> list;
}
