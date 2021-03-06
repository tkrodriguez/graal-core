/*
 * Copyright (c) 2009, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.virtual.nodes;

import java.util.List;

import com.oracle.graal.graph.Node;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.NodeInputList;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.virtual.EscapeObjectState;
import com.oracle.graal.nodes.virtual.VirtualObjectNode;

/**
 * This class encapsulated the virtual state of an escape analyzed object.
 */
@NodeInfo
public final class VirtualObjectState extends EscapeObjectState implements Node.ValueNumberable {

    public static final NodeClass<VirtualObjectState> TYPE = NodeClass.create(VirtualObjectState.class);
    @Input NodeInputList<ValueNode> values;

    public NodeInputList<ValueNode> values() {
        return values;
    }

    public VirtualObjectState(VirtualObjectNode object, ValueNode[] values) {
        super(TYPE, object);
        assert object.entryCount() == values.length;
        this.values = new NodeInputList<>(this, values);
    }

    public VirtualObjectState(VirtualObjectNode object, List<ValueNode> values) {
        super(TYPE, object);
        assert object.entryCount() == values.size();
        this.values = new NodeInputList<>(this, values);
    }

    @Override
    public VirtualObjectState duplicateWithVirtualState() {
        return graph().addWithoutUnique(new VirtualObjectState(object(), values));
    }

    @Override
    public void applyToNonVirtual(NodeClosure<? super ValueNode> closure) {
        for (ValueNode value : values) {
            closure.apply(this, value);
        }
    }
}
