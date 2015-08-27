package com.lib.adaptor;

import java.util.List;

import com.andybrier.lib.R;

import com.iiseeuu.asyncimage.image.ChainImageProcessor;
import com.iiseeuu.asyncimage.image.ImageProcessor;
import com.iiseeuu.asyncimage.image.MaskImageProcessor;
import com.iiseeuu.asyncimage.image.ScaleImageProcessor;
import com.iiseeuu.asyncimage.widget.AsyncImageView;
import com.lib.activity.news.News;
import com.lib.util.LogUtils;
import com.lib.util.StringUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**
 * 新闻资讯Adapter类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewNewsAdapter extends BaseAdapter {

  private static final StringBuilder BUILDER = new StringBuilder();

  private List<News> listItems;// 数据集合
  private LayoutInflater listContainer;// 视图容器
  private int itemViewResource;// 自定义项视图源

  static class ListItemView { // 自定义控件集合
    public TextView title;
    public TextView date;
    public TextView summary;

    public AsyncImageView imageView;
  }


  private ImageProcessor mImageProcessor;

  /**
   * 实例化Adapter
   * 
   * @param context
   * @param data
   * @param resource
   */
  public ListViewNewsAdapter(Context context, List<News> data, int resource) {
    this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
    this.itemViewResource = resource;
    this.listItems = data;

    prepareImageProcessor(context);
  }

  public int getCount() {
    return listItems.size();
  }

  public Object getItem(int arg0) {
    return listItems.get(arg0);
  }

  public long getItemId(int arg0) {
    return 0;
  }

  /**
   * ListView Item设置
   */
  public View getView(int position, View convertView, ViewGroup parent) {
   LogUtils.log("Position", ""+position);

    // 自定义视图
    ListItemView listItemView = null;

    if (convertView == null) {
      // 获取list_item布局文件的视图
      convertView = listContainer.inflate(this.itemViewResource, null);

      listItemView = new ListItemView();
      // 获取控件对象
      listItemView.title = (TextView) convertView.findViewById(R.id.news_listitem_title);
      listItemView.date = (TextView) convertView.findViewById(R.id.news_listitem_date);
      listItemView.summary = (TextView) convertView.findViewById(R.id.news_listitem_summer);

      listItemView.imageView = (AsyncImageView) convertView.findViewById(R.id.async_image);
      listItemView.imageView.setImageProcessor(mImageProcessor);

      // 设置控件集到convertView
      convertView.setTag(listItemView);
    } else {
      listItemView = (ListItemView) convertView.getTag();
    }

    // 设置文字和图片
    News news = listItems.get(position);

    listItemView.title.setText(news.getTitle());
    listItemView.title.setTag(news);// 设置隐藏参数(实体类)
    listItemView.date.setText(news.getTime());
    listItemView.summary.setText(news.getSummer());


    // setUrl后 图片开始去下载
    if (!StringUtils.isEmpty(news.getImageUrl())) {
      listItemView.imageView.setUrl(news.getImageUrl());
    }
    return convertView;
  }


  private void prepareImageProcessor(Context context) {

    final int thumbnailSize = context.getResources().getDimensionPixelSize(R.dimen.thumbnail_size);
    final int thumbnailRadius =
        context.getResources().getDimensionPixelSize(R.dimen.thumbnail_radius);

    
    // if--else 为图片圆角的两种处理方式,可任选一种
    if (Math.random() >= 0.5f) {
      // @formatter:off
      mImageProcessor =
          new ChainImageProcessor(new ScaleImageProcessor(thumbnailSize, thumbnailSize,
              ScaleType.FIT_XY), new MaskImageProcessor(thumbnailRadius));
      // @formatter:on
    } else {

      Path path = new Path();
      path.moveTo(thumbnailRadius, 0);

      path.lineTo(thumbnailSize - thumbnailRadius, 0);
      path.lineTo(thumbnailSize, thumbnailRadius);
      path.lineTo(thumbnailSize, thumbnailSize - thumbnailRadius);
      path.lineTo(thumbnailSize - thumbnailRadius, thumbnailSize);
      path.lineTo(thumbnailRadius, thumbnailSize);
      path.lineTo(0, thumbnailSize - thumbnailRadius);
      path.lineTo(0, thumbnailRadius);

      path.close();

      Bitmap mask = Bitmap.createBitmap(thumbnailSize, thumbnailSize, Config.ARGB_8888);
      Canvas canvas = new Canvas(mask);

      Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
      paint.setStyle(Style.FILL_AND_STROKE);
      paint.setColor(Color.RED);

      canvas.drawPath(path, paint);

      // @formatter:off
      mImageProcessor =
          new ChainImageProcessor(new ScaleImageProcessor(thumbnailSize, thumbnailSize,
              ScaleType.FIT_XY), new MaskImageProcessor(mask));
      // @formatter:on
    }
  }
}
