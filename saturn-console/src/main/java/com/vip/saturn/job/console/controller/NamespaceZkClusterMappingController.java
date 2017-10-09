/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * </p>
 */

package com.vip.saturn.job.console.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vip.saturn.job.console.domain.MoveNamespaceBatchStatus;
import com.vip.saturn.job.console.domain.NamespaceZkClusterMappingVo;
import com.vip.saturn.job.console.domain.RequestResult;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.service.NamespaceZkClusterMappingService;
import com.vip.saturn.job.console.utils.SessionAttributeKeys;

@RestController
@RequestMapping("registry_center")
public class NamespaceZkClusterMappingController extends AbstractController {

	private final static Logger LOGGER = LoggerFactory.getLogger(NamespaceZkClusterMappingController.class);

	@Resource
	private NamespaceZkClusterMappingService namespaceZkClusterMappingService;

	@RequestMapping(value = "getNamespaceZkclusterMappingList", method = RequestMethod.GET)
	public RequestResult getNamespaceZkclusterMappingList(HttpServletRequest request) {
		RequestResult requestResult = new RequestResult();
		try {
			List<NamespaceZkClusterMappingVo> namespaceZkclusterMappingList = namespaceZkClusterMappingService
					.getNamespaceZkClusterMappingList();
			requestResult.setObj(namespaceZkclusterMappingList);
			requestResult.setSuccess(true);
		} catch (SaturnJobConsoleException e) {
			requestResult.setSuccess(false);
			requestResult.setMessage(e.getMessage());
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}

	@RequestMapping(value = "initNamespaceZkClusterMapping", method = RequestMethod.POST)
	public RequestResult initNamespaceZkClusterMapping(HttpServletRequest request) {
		RequestResult requestResult = new RequestResult();
		try {
			namespaceZkClusterMappingService.initNamespaceZkClusterMapping("");
			requestResult.setSuccess(true);
		} catch (SaturnJobConsoleException e) {
			requestResult.setSuccess(false);
			requestResult.setMessage(e.getMessage());
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}

	@RequestMapping(value = "getZkClusterListWithOnline", method = RequestMethod.GET)
	public RequestResult getZkClusterListWithOnline(HttpServletRequest request) {
		RequestResult requestResult = new RequestResult();
		try {
			List<String> zkClusterListWithOnlineFromCfg = namespaceZkClusterMappingService.getZkClusterListWithOnline();
			requestResult.setObj(zkClusterListWithOnlineFromCfg);
			requestResult.setSuccess(true);
		} catch (SaturnJobConsoleException e) {
			requestResult.setSuccess(false);
			requestResult.setMessage(e.getMessage());
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}

	@RequestMapping(value = "moveNamespaceBatch", method = RequestMethod.POST)
	public RequestResult moveNamespaceBatch(HttpServletRequest request, String namespaces, String zkClusterKeyNew,
			boolean updateDBOnly, long id) {
		RequestResult requestResult = new RequestResult();
		try {
			if (namespaces == null || namespaces.trim().isEmpty()) {
				throw new SaturnJobConsoleException("The namespace cannot be null");
			}
			if (zkClusterKeyNew == null || zkClusterKeyNew.trim().isEmpty()) {
				throw new SaturnJobConsoleException("The zkClusterKeyNew cannot be null");
			}
			MoveNamespaceBatchStatus moveNamespaceBatchStatus = namespaceZkClusterMappingService
					.moveNamespaceBatchTo(namespaces, zkClusterKeyNew, "", updateDBOnly);
			request.getSession().setAttribute(SessionAttributeKeys.getMoveNamespaceBatchStatusKey(id), moveNamespaceBatchStatus);
			requestResult.setSuccess(true);
		} catch (SaturnJobConsoleException e) {
			requestResult.setSuccess(false);
			requestResult.setMessage(e.getMessage());
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}

	@RequestMapping(value = "getMoveNamespaceBatchStatus", method = RequestMethod.GET)
	public RequestResult getMoveNamespaceBatchStatus(HttpServletRequest request, long id) {
		RequestResult requestResult = new RequestResult();
		try {
			Thread.sleep(400L);
			String moveNamespaceBatchStatusKey = SessionAttributeKeys.getMoveNamespaceBatchStatusKey(id);
			MoveNamespaceBatchStatus moveNamespaceBatchStatus = (MoveNamespaceBatchStatus) request.getSession()
					.getAttribute(moveNamespaceBatchStatusKey);
			if(moveNamespaceBatchStatus == null) {
				throw new SaturnJobConsoleException("The MoveNamespaceBatchStatus is not existing in session, id is " + id);
			}
			if(moveNamespaceBatchStatus.isFinished()) {
				request.getSession().removeAttribute(moveNamespaceBatchStatusKey);
			}
			requestResult.setSuccess(true);
			requestResult.setObj(moveNamespaceBatchStatus);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}

	@RequestMapping(value = "clearMoveNamespaceBatchStatus", method = RequestMethod.GET)
	public RequestResult clearMoveNamespaceBatchStatus(HttpServletRequest request, long id) {
		RequestResult requestResult = new RequestResult();
		try {
			request.getSession().removeAttribute(SessionAttributeKeys.getMoveNamespaceBatchStatusKey(id));
			requestResult.setSuccess(true);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			requestResult.setSuccess(false);
			requestResult.setMessage(t.toString());
		}
		return requestResult;
	}
}
