package com.kuaikan.app.scenecollection.parse;

import com.kuaikan.app.scenecollection.bean.Result;

import java.io.InputStream;
import java.util.List;

/**
 * Created by gejun on 2017/1/5.
 */

public interface XmlParser {
    public List<Result> parse(InputStream is) throws Exception;
}
