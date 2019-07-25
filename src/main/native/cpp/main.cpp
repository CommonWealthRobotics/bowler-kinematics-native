/*
 * This file is part of bowler-kinematics-native.
 *
 * bowler-kinematics-native is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bowler-kinematics-native is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with bowler-kinematics-native.  If not, see <https://www.gnu.org/licenses/>.
 */
#include "cppoptlib/meta.h"
#include "cppoptlib/problem.h"
#include "cppoptlib/solver/lbfgsbsolver.h"
#include <cmath>
#include <jni.h>

template <typename Scalar> class DhParam {
  public:
  DhParam(Scalar d, Scalar theta, Scalar r, Scalar alpha) : d(d), theta(theta), r(r), alpha(alpha) {
    ft.coeffRef(2, 0) = 0;
    ft.coeffRef(2, 3) = d;
    ft.coeffRef(3, 0) = 0;
    ft.coeffRef(3, 1) = 0;
    ft.coeffRef(3, 2) = 0;
    ft.coeffRef(3, 3) = 1;
  }

  void computeFT(const Scalar jointAngle) {
    const Scalar ct = std::cos(theta + jointAngle);
    const Scalar st = std::sin(theta + jointAngle);
    const Scalar ca = std::cos(alpha);
    const Scalar sa = std::sin(alpha);
    ft.coeffRef(0, 0) = ct;
    ft.coeffRef(0, 1) = -st * ca;
    ft.coeffRef(0, 2) = st * sa;
    ft.coeffRef(0, 3) = r * ct;
    ft.coeffRef(1, 0) = st;
    ft.coeffRef(1, 1) = ct * ca;
    ft.coeffRef(1, 2) = -ct * sa;
    ft.coeffRef(1, 3) = r * st;
    ft.coeffRef(2, 1) = sa;
    ft.coeffRef(2, 2) = ca;
  }

  const Scalar d;
  const Scalar theta;
  const Scalar r;
  const Scalar alpha;
  Eigen::Matrix<Scalar, 4, 4> ft;
};

template <typename T> class IkProblem : public cppoptlib::BoundedProblem<T> {
  public:
  using typename cppoptlib::Problem<T>::Scalar;
  using typename cppoptlib::Problem<T>::TVector;
  using FT = Eigen::Matrix<Scalar, 4, 4>;

  IkProblem(std::vector<DhParam<T>> dhParams, const FT target, const TVector &l, const TVector &u)
    : cppoptlib::BoundedProblem<T>(l, u), target(std::move(target)), dhParams(std::move(dhParams)) {
  }

  T value(const TVector &jointAngles) override {
    FT tip = FT::Identity();
    for (size_t i = 0; i < dhParams.size(); ++i) {
      dhParams[i].computeFT(jointAngles[i]);
      tip *= dhParams[i].ft;
    }

    const T targetX = target.coeff(0, 3);
    const T targetY = target.coeff(1, 3);
    const T targetZ = target.coeff(2, 3);

    const T tipX = tip.coeff(0, 3);
    const T tipY = tip.coeff(1, 3);
    const T tipZ = tip.coeff(2, 3);

    return std::sqrt(std::pow(tipX - targetX, 2) + std::pow(tipY - targetY, 2) +
                     std::pow(tipZ - targetZ, 2));
  }

  void gradient(const TVector &x, TVector &grad) override {
    // TODO: Compute jacobian
    cppoptlib::Problem<T>::finiteGradient(x, grad, 0);
  }

  const FT target;
  std::vector<DhParam<T>> dhParams;
};

using IkProblemf = IkProblem<float>;

extern "C" {
JNIEXPORT jfloatArray JNICALL
Java_com_neuronrobotics_bowlerkinematicsnative_solver_NativeIKSolver_solve(JNIEnv *env,
                                                                           jobject object,
                                                                           jint numberOfLinks,
                                                                           jfloatArray dataArray) {
  jfloat *data = env->GetFloatArrayElements(dataArray, 0);
  const int dhParamsOffset = 0;
  std::vector<DhParam<float>> dhParams;
  dhParams.reserve(numberOfLinks);
  for (int i = 0; i < numberOfLinks; ++i) {
    dhParams.emplace_back(data[i * 4 + 0 + dhParamsOffset],
                          data[i * 4 + 1 + dhParamsOffset],
                          data[i * 4 + 2 + dhParamsOffset],
                          data[i * 4 + 3 + dhParamsOffset]);
  }

  const int upperJointLimitsOffset = dhParamsOffset + numberOfLinks * 4;
  Eigen::VectorXf upperJointLimits(numberOfLinks);
  for (int i = 0; i < numberOfLinks; ++i) {
    upperJointLimits.coeffRef(i) = data[i + upperJointLimitsOffset];
  }

  const int lowerJointLimitsOffset = upperJointLimitsOffset + numberOfLinks;
  Eigen::VectorXf lowerJointLimits(numberOfLinks);
  for (int i = 0; i < numberOfLinks; ++i) {
    lowerJointLimits.coeffRef(i) = data[i + lowerJointLimitsOffset];
  }

  const int targetOffset = lowerJointLimitsOffset + numberOfLinks;
  Eigen::Matrix4f target;
  for (int i = 0; i < 16; ++i) {
    const int row = i / 4;
    const int col = i % 4;
    target.coeffRef(row, col) = data[i + targetOffset];
  }

  const int initialJointAnglesOffset = targetOffset + 16;
  Eigen::VectorXf initialJointAngles(numberOfLinks);
  for (int i = 0; i < numberOfLinks; ++i) {
    initialJointAngles.coeffRef(i) = data[i + initialJointAnglesOffset];
  }

  IkProblemf f(std::move(dhParams), std::move(target), lowerJointLimits, upperJointLimits);

  cppoptlib::LbfgsbSolver<IkProblemf> solver;
  solver.minimize(f, initialJointAngles);
  env->ReleaseFloatArrayElements(dataArray, data, 0);
  jfloatArray result = env->NewFloatArray(numberOfLinks);
  env->SetFloatArrayRegion(result, 0, numberOfLinks, initialJointAngles.data());
  return result;
}
}
