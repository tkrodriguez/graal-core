/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.hotspot.nodes;

import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.meta.ResolvedJavaType;

import com.oracle.graal.compiler.common.type.StampPair;
import com.oracle.graal.compiler.common.type.TypeReference;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.spi.Canonicalizable;
import com.oracle.graal.graph.spi.CanonicalizerTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.CallTargetNode.InvokeKind;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.java.CheckCastNode;
import com.oracle.graal.replacements.nodes.MacroNode;
import com.oracle.graal.replacements.nodes.MacroStateSplitNode;

/**
 * {@link MacroNode Macro node} for {@link Class#cast(Object)}.
 */
@NodeInfo
public final class ClassCastNode extends MacroStateSplitNode implements Canonicalizable.Binary<ValueNode> {

    public static final NodeClass<ClassCastNode> TYPE = NodeClass.create(ClassCastNode.class);

    public ClassCastNode(InvokeKind invokeKind, ResolvedJavaMethod targetMethod, int bci, StampPair returnStamp, ValueNode receiver, ValueNode object) {
        super(TYPE, invokeKind, targetMethod, bci, returnStamp, receiver, object);
    }

    private ValueNode getJavaClass() {
        return arguments.get(0);
    }

    private ValueNode getObject() {
        return arguments.get(1);
    }

    public ValueNode getX() {
        return getJavaClass();
    }

    public ValueNode getY() {
        return getObject();
    }

    @Override
    public ValueNode canonical(CanonicalizerTool tool, ValueNode forJavaClass, ValueNode forObject) {
        ValueNode folded = tryFold(forJavaClass, forObject, tool.getConstantReflection());
        return folded != null ? folded : this;
    }

    public static ValueNode tryFold(ValueNode javaClass, ValueNode object, ConstantReflectionProvider constantReflection) {
        if (javaClass != null && javaClass.isConstant()) {
            ResolvedJavaType type = constantReflection.asJavaType(javaClass.asConstant());
            if (type != null && !type.isPrimitive()) {
                return CheckCastNode.create(TypeReference.createTrusted(javaClass.graph().getAssumptions(), type), object, null, false);
            }
        }
        return null;
    }
}
