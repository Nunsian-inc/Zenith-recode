package org.zenith;

import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.utility.api.JointCodebook;
import org.zenith.utility.misc.WindowAccess;

public interface ZenithAccess extends WindowAccess {
   Zenith zenith = Zenith.getInstance();
   JointCodebook jointCodebook = Zenith.getInstance().getJointCodebook();
   RotationManager rotationManager = Zenith.getInstance().getRotationManager();
   BcCameraTestnerv bcCameraTestnerv = rotationManager.getBcCameraTestnerv();
}
