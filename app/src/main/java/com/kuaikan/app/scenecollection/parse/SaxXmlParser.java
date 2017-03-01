package com.kuaikan.app.scenecollection.parse;

import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.bean.GsmResult;
import com.kuaikan.app.scenecollection.bean.Result;
import com.kuaikan.app.scenecollection.util.Util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by gejun on 2017/1/5.
 */

public class SaxXmlParser implements XmlParser{
    @Override
    public List<Result> parse(InputStream is) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();  //取得SAXParserFactory实例
        SAXParser parser = factory.newSAXParser();                  //从factory获取SAXParser实例
        MyHandler handler = new MyHandler();                        //实例化自定义Handler
        parser.parse(is, handler);                                  //根据自定义Handler规则解析输入流
        return handler.getCellResult();
    }

    //需要重写DefaultHandler的方法
    private class MyHandler extends DefaultHandler {

        private List<Result> cells;
        private Result cell;
        private StringBuilder builder;

        //返回解析后得到的Book对象集合
        public List<Result> getCellResult() {
            return cells;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            cells = new ArrayList<Result>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals(Result.RAT_4G)
                    || localName.equals(Result.RAT_2G)
                    || localName.equals(Result.RAT_3G_CU)
                    || localName.equals(Result.RAT_3G_CMCC)) {
                cell = new GsmResult();
            } else if(localName.equals(Result.RAT_CDMA)){
                cell = new CdmaResult();
            }
            builder.setLength(0);//将字符长度设置为0 以便重新开始读取元素内的字符节点
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);//将读取的字符数组追加到builder中
//            Log.i("gejun","characters = " + builder.toString());
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            String value = builder.toString();
            if (localName.equals(Result.LAC)) {
                ((GsmResult)cell).setLac(value);
            } else if (localName.equals(Result.CID)) {
                ((GsmResult)cell).setCellId(value);
            } else if (localName.equals(Result.SIG1)) {
                ((GsmResult)cell).setRssi(value);
            } else if (localName.equals(Result.SID)) {
                ((CdmaResult)cell).setSid(value);
            } else if (localName.equals(Result.NID)){
                ((CdmaResult)cell).setNid(value);
            } else if (localName.equals(Result.BID)){
                ((CdmaResult)cell).setBid(value);
            } else if (localName.equals(Result.PN)){
                ((CdmaResult)cell).setPn(value);
            } else if (localName.equals(Result.RX)){
                ((CdmaResult)cell).setRx(value);
            } else if(localName.equals(Result.MNC)){
                cell.setMnc(value);
            } else if(localName.equals(Result.RAT_4G)){
                cell.setRat(Util.G4);
                cells.add(cell);
            } else if(localName.equals(Result.RAT_3G_CMCC)
                    || localName.equals(Result.RAT_3G_CU)){
                cell.setRat(Util.G3);
                cells.add(cell);
            } else if(localName.equals(Result.RAT_2G)){
                cell.setRat(Util.G2);
                cells.add(cell);
            } else if(localName.equals(Result.RAT_CDMA)){
                cell.setRat("-1");
                cells.add(cell);
            }
        }
    }
}
