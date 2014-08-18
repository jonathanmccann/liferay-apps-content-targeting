/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.contenttargeting.rule.gender;

import com.liferay.portal.contenttargeting.anonymoususers.model.AnonymousUser;
import com.liferay.portal.contenttargeting.api.model.BaseRule;
import com.liferay.portal.contenttargeting.api.model.Rule;
import com.liferay.portal.contenttargeting.model.RuleInstance;
import com.liferay.portal.contenttargeting.rulecategories.UserAttributesRuleCategory;
import com.liferay.portal.contenttargeting.util.ContentTargetingRuleUtil;
import com.liferay.portal.contenttargeting.util.PortletKeys;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Julio Camarero
 */
@Component(immediate = true, service = Rule.class)
public class GenderRule extends BaseRule {

	@Activate
	@Override
	public void activate() {
		super.activate();
	}

	@Deactivate
	@Override
	public void deActivate() {
		super.deActivate();
	}

	@Override
	public boolean evaluate(
			HttpServletRequest request, RuleInstance ruleInstance,
			AnonymousUser anonymousUser)
		throws Exception {

		User user = anonymousUser.getUser();

		if (user == null) {
			return false;
		}

		String gender = ruleInstance.getTypeSettings();

		if ((user.isMale() && gender.equals("male")) ||
			!user.isMale() && gender.equals("female")) {

			return true;
		}

		return false;
	}

	@Override
	public String getIcon() {
		return "icon-female";
	}

	@Override
	public String getRuleCategoryKey() {
		return UserAttributesRuleCategory.KEY;
	}

	@Override
	public String getSummary(RuleInstance ruleInstance, Locale locale) {
		return LanguageUtil.get(locale, ruleInstance.getTypeSettings());
	}

	@Override
	public String processRule(
			PortletRequest request, PortletResponse response, String id,
			Map<String, String> values)
		throws Exception {

		return values.get("gender");
	}

	@Override
	protected void populateContext(
		RuleInstance ruleInstance, Map<String, Object> context) {

		String gender = "male";

		if (ruleInstance != null) {
			gender = ruleInstance.getTypeSettings();
		}

		context.put("gender", gender);

		boolean genderEnabled = false;

		Company company = (Company)context.get("company");

		try {
			genderEnabled = PrefsPropsUtil.getBoolean(
				company.getCompanyId(),
				PropsKeys.FIELD_ENABLE_COM_LIFERAY_PORTAL_MODEL_CONTACT_MALE);
		}
		catch (SystemException se) {
		}

		context.put("genderEnabled", genderEnabled);

		boolean hasPortalSettingsViewPermission = false;

		if (!genderEnabled) {
			hasPortalSettingsViewPermission =
				ContentTargetingRuleUtil.hasControlPanelPortletViewPermission(
					context, PortletKeys.PORTAL_SETTINGS);
		}

		context.put(
			"hasPortalSettingsViewPermission", hasPortalSettingsViewPermission);
	}

}