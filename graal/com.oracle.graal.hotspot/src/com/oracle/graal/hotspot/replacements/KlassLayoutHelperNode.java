/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.hotspot.replacements;

import jdk.vm.ci.hotspot.HotSpotResolvedObjectType;
import jdk.vm.ci.hotspot.HotSpotVMConfig;
import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaType;

import com.oracle.graal.compiler.common.type.ObjectStamp;
import com.oracle.graal.compiler.common.type.Stamp;
import com.oracle.graal.compiler.common.type.StampFactory;
import com.oracle.graal.graph.Node;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.spi.Canonicalizable;
import com.oracle.graal.graph.spi.CanonicalizerTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ConstantNode;
import com.oracle.graal.nodes.FloatingGuardedNode;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.extended.GuardingNode;
import com.oracle.graal.nodes.extended.LoadHubNode;
import com.oracle.graal.nodes.spi.Lowerable;
import com.oracle.graal.nodes.spi.LoweringTool;

/**
 * Read {@code Klass::_layout_helper} and incorporate any useful stamp information based on any type
 * information in {@code klass}.
 */
@NodeInfo
public final class KlassLayoutHelperNode extends FloatingGuardedNode implements Canonicalizable, Lowerable {

    public static final NodeClass<KlassLayoutHelperNode> TYPE = NodeClass.create(KlassLayoutHelperNode.class);
    @Input protected ValueNode klass;
    protected final HotSpotVMConfig config;

    public KlassLayoutHelperNode(@InjectedNodeParameter HotSpotVMConfig config, ValueNode klass) {
        this(config, klass, null);
    }

    public KlassLayoutHelperNode(@InjectedNodeParameter HotSpotVMConfig config, ValueNode klass, ValueNode guard) {
        super(TYPE, StampFactory.forKind(JavaKind.Int), (GuardingNode) guard);
        this.klass = klass;
        this.config = config;
    }

    @Override
    public boolean inferStamp() {
        if (klass instanceof LoadHubNode) {
            LoadHubNode hub = (LoadHubNode) klass;
            Stamp hubStamp = hub.getValue().stamp();
            if (hubStamp instanceof ObjectStamp) {
                ObjectStamp objectStamp = (ObjectStamp) hubStamp;
                ResolvedJavaType type = objectStamp.type();
                if (type != null && !type.isJavaLangObject()) {
                    if (!type.isArray() && !type.isInterface()) {
                        /*
                         * Definitely some form of instance type.
                         */
                        return updateStamp(StampFactory.forInteger(JavaKind.Int, config.klassLayoutHelperNeutralValue, Integer.MAX_VALUE));
                    }
                    if (type.isArray()) {
                        return updateStamp(StampFactory.forInteger(JavaKind.Int, Integer.MIN_VALUE, config.klassLayoutHelperNeutralValue - 1));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Node canonical(CanonicalizerTool tool) {
        if (tool.allUsagesAvailable() && hasNoUsages()) {
            return null;
        } else {
            if (klass.isConstant()) {
                if (!klass.asConstant().isDefaultForKind()) {
                    Constant constant = stamp().readConstant(tool.getConstantReflection().getMemoryAccessProvider(), klass.asConstant(), config.klassLayoutHelperOffset);
                    return ConstantNode.forConstant(stamp(), constant, tool.getMetaAccess());
                }
            }
            if (klass instanceof LoadHubNode) {
                LoadHubNode hub = (LoadHubNode) klass;
                Stamp hubStamp = hub.getValue().stamp();
                if (hubStamp instanceof ObjectStamp) {
                    ObjectStamp ostamp = (ObjectStamp) hubStamp;
                    HotSpotResolvedObjectType type = (HotSpotResolvedObjectType) ostamp.type();
                    if (type != null && type.isArray() && !type.getComponentType().isPrimitive()) {
                        // The layout for all object arrays is the same.
                        Constant constant = stamp().readConstant(tool.getConstantReflection().getMemoryAccessProvider(), type.klass(), config.klassLayoutHelperOffset);
                        return ConstantNode.forConstant(stamp(), constant, tool.getMetaAccess());
                    }
                }
            }
            return this;
        }
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    public ValueNode getHub() {
        return klass;
    }
}
