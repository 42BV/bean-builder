[![Build Status](https://travis-ci.org/42BV/beanie.svg?branch=master)](https://travis-ci.org/42BV/beanie)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/86775ea7cd154b4c89547f4b9533ea52)](https://www.codacy.com/app/42bv/beanie)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/nl.42/beanie/badge.svg)](https://maven-badges.herokuapp.com/maven-central/nl.42/beanie)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/nl.42/beanie/badge.svg)](http://www.javadoc.io/doc/nl.42/beanie)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Library for generating and testing beans.

Testing
-------
Don't you find it annoying testing getters and setters and nullary constructors all the time? No more!
new BeanTester().includeAll().verifyBeans("some.package");

License
-------
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

