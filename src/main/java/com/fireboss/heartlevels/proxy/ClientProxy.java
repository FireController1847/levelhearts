package com.fireboss.heartlevels.proxy;

import com.fireboss.heartlevels.init.InitItems;

public class ClientProxy implements CommonProxy {

	@Override
	public void RenderItems() {
		InitItems.Render();
	}

}
