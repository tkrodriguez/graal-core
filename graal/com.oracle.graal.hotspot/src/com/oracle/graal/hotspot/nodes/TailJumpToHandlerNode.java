/*
 * Copyright (c) 2015, 2015, Oracle and/or its affiliates. All rights reserved.
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

import com.oracle.graal.compiler.common.spi.ForeignCallDescriptor;
import com.oracle.graal.compiler.common.spi.ForeignCallLinkage;
import com.oracle.graal.compiler.common.spi.ForeignCallsProvider;
import com.oracle.graal.compiler.common.type.StampFactory;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.hotspot.HotSpotNodeLIRBuilder;
import com.oracle.graal.hotspot.stubs.ExceptionHandlerStub;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ControlSinkNode;
import com.oracle.graal.nodes.spi.LIRLowerable;
import com.oracle.graal.nodes.spi.NodeLIRBuilderTool;

/**
 * Jumps to the handler specified by {@link #handler}. This node is specific for the
 * {@link ExceptionHandlerStub} and should not be used elsewhere.
 */
@NodeInfo
public final class TailJumpToHandlerNode extends ControlSinkNode implements LIRLowerable {

    public static final NodeClass<TailJumpToHandlerNode> TYPE = NodeClass.create(TailJumpToHandlerNode.class);

    private final ForeignCallLinkage handler;

    public TailJumpToHandlerNode(@InjectedNodeParameter ForeignCallsProvider foreignCalls, ForeignCallDescriptor descriptor) {
        super(TYPE, StampFactory.forVoid());
        this.handler = foreignCalls.lookupForeignCall(descriptor);
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        ((HotSpotNodeLIRBuilder) gen).emitTailJumpToHandler(handler);
    }

    @NodeIntrinsic
    public static native void tailJumpToHandler(@ConstantNodeParameter ForeignCallDescriptor descriptor);
}
