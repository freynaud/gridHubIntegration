package org.uiautomation;

import java.util.Map;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.IOSDriver;

public class IOSAugmentedCapabilityMatcher extends DefaultCapabilityMatcher{
  @Override
  public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
   
    // IOS request
    if (requestedCapability.containsKey(IOSCapabilities.DEVICE)){
      return IOSDriver.matches(nodeCapability, requestedCapability);
    }else{
      return super.matches(nodeCapability, requestedCapability);
    }
   
  }

}
