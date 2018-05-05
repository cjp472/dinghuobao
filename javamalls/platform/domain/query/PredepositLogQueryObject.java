package com.javamalls.platform.domain.query;

import com.javamalls.base.query.QueryObject;

import org.springframework.web.servlet.ModelAndView;

public class PredepositLogQueryObject
  extends QueryObject
{
  public PredepositLogQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
  {
    super(currentPage, mv, orderBy, orderType);
  }
  
  public PredepositLogQueryObject() {}
}

 