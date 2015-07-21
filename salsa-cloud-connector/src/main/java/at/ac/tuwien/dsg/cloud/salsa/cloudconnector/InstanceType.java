/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector;

public enum InstanceType {
	DEFAULT ("m1.small"),
    LARGE ("m1.large"),
    XLARGE ("m1.xlarge"),
    MEDIUM_HCPU ("c1.medium"),
    XLARGE_HCPU ("c1.xlarge"),
    XLARGE_HMEM ("m2.xlarge"),
    XLARGE_DOUBLE_HMEM ("m2.2xlarge"),
    XLARGE_QUAD_HMEM ("m2.4xlarge");

    private final String typeId;

    InstanceType(String typeId) {
            this.typeId = typeId;
    }

    public String getTypeId() {
            return typeId;
    }

    public static InstanceType getTypeFromString(String val) {
            for (InstanceType t : InstanceType.values()) {
                    if (t.getTypeId().equals(val)) {
                            return t;
                    }
            }
            return null;
    }
}
