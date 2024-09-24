package org.example.basewebsub.controller.bases;

import org.example.basewebsub.auth.beans.UserAuthProvider;
import org.example.basewebsub.auth.models.UserPrincipal;
import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.bases.ILogManage;
import org.example.basewebsub.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class BaseController {
	protected static final ILogManage logger = LogManage.getLogManage(BaseController.class);

	@Autowired
	UserAuthProvider userAuthProvider;

	protected UserPrincipal getUserPrincipal() {
		return userAuthProvider.getUserPrincipal();
	}
	protected String getUsername() {
		if (userAuthProvider != null && StringUtil.isNotNullAndEmpty(userAuthProvider.getUsername())) {
			return userAuthProvider.getUsername().toLowerCase(Locale.ROOT);
		} else {
			return null;
		}
	}

	protected Long getUserId() {
		return userAuthProvider.getUserId();
	}

}