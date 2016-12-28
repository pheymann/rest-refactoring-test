package com.github.pheymann.rrtt

import com.github.pheymann.rrtt.TestRunner.TestRequest
import com.github.pheymann.rrtt.util.ResponseComparator.ComparisonResult

final case class TestResult[R <: TestRequest](
                                               name: String,

                                               successful: Boolean,
                                               successfulTries: Int,

                                               failedTries: Int,
                                               comparisons: List[(R, ComparisonResult)]
                                             )
