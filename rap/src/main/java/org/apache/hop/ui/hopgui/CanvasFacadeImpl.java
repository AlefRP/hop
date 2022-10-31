/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.ui.hopgui;

import org.apache.hop.base.AbstractMeta;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.DPoint;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.pipeline.PipelineHopMeta;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.PipelineSvgPainter;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.hopgui.file.workflow.HopGuiWorkflowGraph;
import org.apache.hop.workflow.WorkflowHopMeta;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.WorkflowSvgPainter;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.Canvas;

public class CanvasFacadeImpl extends CanvasFacade {

  @Override
  void setDataInternal(
      Canvas canvas,
      float magnification,
      DPoint offset,
      AbstractMeta meta,
      Class<?> type,
      IVariables variables) {
    setDataCommon(canvas, magnification, offset, meta);
    if (type == HopGuiWorkflowGraph.class) {
      setDataWorkflow(canvas, magnification, meta, variables);
    } else {
      setDataPipeline(canvas, magnification, meta, variables);
    }
  }

  private void setDataCommon(Canvas canvas, float magnification, DPoint offset, AbstractMeta meta) {
    JsonObject jsonProps = new JsonObject();
    jsonProps.add("themeId", System.getProperty(HopWeb.HOP_WEB_THEME, "light"));
    jsonProps.add(
        "gridSize",
        PropsUi.getInstance().isShowCanvasGridEnabled()
            ? PropsUi.getInstance().getCanvasGridSize()
            : 1);
    jsonProps.add("iconSize", PropsUi.getInstance().getIconSize());
    jsonProps.add("magnification", (float) (magnification * PropsUi.getNativeZoomFactor()));
    jsonProps.add("offsetX", offset.x);
    jsonProps.add("offsetY", offset.y);
    canvas.setData("props", jsonProps);

    JsonArray jsonNotes = new JsonArray();
    meta.getNotes()
        .forEach(
            note -> {
              JsonObject jsonNote = new JsonObject();
              jsonNote.add("x", note.getLocation().x);
              jsonNote.add("y", note.getLocation().y);
              jsonNote.add("width", note.getWidth());
              jsonNote.add("height", note.getHeight());
              jsonNote.add("selected", note.isSelected());
              jsonNote.add("note", note.getNote());
              jsonNotes.add(jsonNote);
            });
    canvas.setData("notes", jsonNotes);
  }

  private void setDataWorkflow(
      Canvas canvas, float magnification, AbstractMeta meta, IVariables variables) {
    WorkflowMeta workflowMeta = (WorkflowMeta) meta;
    JsonObject jsonNodes = new JsonObject();

    try {
      String svg = WorkflowSvgPainter.generateWorkflowSvg(workflowMeta, magnification, variables);
      canvas.setData("svg", svg);
    } catch (HopException e) {
      LogChannel.UI.logError("Error generating workflow SVG", e);
    }

    // Store the individual SVG images of the actions as well
    //
    workflowMeta
        .getActions()
        .forEach(
            actionMeta -> {
              JsonObject jsonNode = new JsonObject();
              jsonNode.add("x", actionMeta.getLocation().x);
              jsonNode.add("y", actionMeta.getLocation().y);
              jsonNode.add("selected", actionMeta.isSelected());

              jsonNodes.add(actionMeta.getName(), jsonNode);
            });
    canvas.setData("nodes", jsonNodes);

    JsonArray jsonHops = new JsonArray();
    for (int i = 0; i < workflowMeta.nrWorkflowHops(); i++) {
      JsonObject jsonHop = new JsonObject();
      WorkflowHopMeta hop = workflowMeta.getWorkflowHop(i);
      jsonHop.add("from", hop.getFromAction().getName());
      jsonHop.add("to", hop.getToAction().getName());
      jsonHops.add(jsonHop);
    }
    canvas.setData("hops", jsonHops);
  }

  private void setDataPipeline(
      Canvas canvas, float magnification, AbstractMeta meta, IVariables variables) {
    PipelineMeta pipelineMeta = (PipelineMeta) meta;
    JsonObject jsonNodes = new JsonObject();

    try {
      String svg = PipelineSvgPainter.generatePipelineSvg(pipelineMeta, magnification, variables);
      canvas.setData("svg", svg);
    } catch (HopException e) {
      LogChannel.UI.logError("Error generating pipeline SVG", e);
    }

    pipelineMeta
        .getTransforms()
        .forEach(
            transformMeta -> {
              JsonObject jsonNode = new JsonObject();
              jsonNode.add("x", transformMeta.getLocation().x);
              jsonNode.add("y", transformMeta.getLocation().y);
              jsonNode.add("selected", transformMeta.isSelected());

              jsonNodes.add(transformMeta.getName(), jsonNode);
            });
    canvas.setData("nodes", jsonNodes);

    JsonArray jsonHops = new JsonArray();
    for (int i = 0; i < pipelineMeta.nrPipelineHops(); i++) {
      JsonObject jsonHop = new JsonObject();
      PipelineHopMeta hop = pipelineMeta.getPipelineHop(i);
      if (hop.getFromTransform() != null && hop.getToTransform() != null) {
        jsonHop.add("from", hop.getFromTransform().getName());
        jsonHop.add("to", hop.getToTransform().getName());
        jsonHops.add(jsonHop);
      }
    }
    canvas.setData("hops", jsonHops);
  }
}
