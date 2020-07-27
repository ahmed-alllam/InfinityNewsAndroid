package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Source;

import java.util.List;

@Dao
public interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSources(List<Source> sources);

    @Query("SELECT * FROM source WHERE slug IN(:sourceSlugs)")
    List<Source> getSources(List<String> sourceSlugs);
}
