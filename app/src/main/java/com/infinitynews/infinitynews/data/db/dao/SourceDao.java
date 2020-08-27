package com.infinitynews.infinitynews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.infinitynews.infinitynews.data.models.Source;

import java.util.List;

@Dao
public interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSources(List<Source> sources);

    @Query("SELECT * FROM source WHERE slug IN(:sourceSlugs)")
    List<Source> getSourcesBySlugs(List<String> sourceSlugs);
}
