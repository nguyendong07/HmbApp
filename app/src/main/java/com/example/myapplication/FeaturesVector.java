
package com.example.myapplication;

public class FeaturesVector {
    //Vehicle
    private String vehicle;
    private String status;
    //Features
    private double varianceX;
    private double varianceY;
    private double varianceZ;
    private double meanX;
    private double meanY;
    private double meanZ;
    private double standardDeviationX;
    private double standardDeviationY;
    private double standardDeviationZ;
    private double averageResultantAcceleration;
    private double meanXYZ;
    private double distributionX;
    private double distributionY;
    private double distributionZ;
    private double covarianceXY;
    private double covarianceYZ;
    private double covarianceZX;
    private double zeroCrossingRateX;
    private double zeroCrossingRateY;
    private double zeroCrossingRateZ;
    private double fftEnergyX;
    private double fftEnergyY;
    private double fftEnergyZ;
    private double fftEntropyX;
    private double fftEntropyY;
    private double fftEntropyZ;

    //
    private double activity;
    private double complexity;
    private double mobility;
    private double energy;
    
    // add 8 feature 20170801 Nhacld
    private double parX ;
    private double parY;
    private double parZ;
    private double sma;
    private double smaX;
    private double smaY;
    private double smaZ;
    private double dsvm;
    // add 5 feature per Activity,Mobility, Complexity with x,y,z, phi, theta
    
    private double activityX;
    private double activityY;
    private double activityZ;
    private double activityPhi;
    private double activityTheta;

    private double mobilityX;
    private double mobilityY;
    private double mobilityZ;
    private double mobilityPhi;
    private double mobilityTheta;
    
    private double complexityX;
    private double complexityY;
    private double complexityZ;
    private double complexityPhi;
    private double complexityTheta;
    
    //
    private double meanPhi;
    private double meanTheta;
    private double variancePhi;
    private double varianceTheta;
    //
    private double igPHI;
    private double igTHETA;
    
    //update new features;
    private double varPhiOren;
    private double avaPhiOren;
    private double varLamdaOren;
    private double avaLamdaOren;
    
    
    //bo sung feature wavelet
    
    //MavWaveletXLevelOneA AvpWaveletXLevelOneA
    
    private double mavWaveletXLevelOneA;
    private double mavWaveletXLevelOneD;
    private double avpWaveletXLevelOneA;
    private double avpWaveletXLevelOneD;
    private double skWaveletXLevelOneA;
    private double skWaveletXLevelOneD;
    
    //Y
    private double mavWaveletYLevelOneA;
    private double mavWaveletYLevelOneD;
    private double avpWaveletYLevelOneA;
    private double avpWaveletYLevelOneD;
    private double skWaveletYLevelOneA;
    private double skWaveletYLevelOneD;
    
    //Z
    private double mavWaveletZLevelOneA;
    private double mavWaveletZLevelOneD;
    private double avpWaveletZLevelOneA;
    private double avpWaveletZLevelOneD;
    private double skWaveletZLevelOneA;
    private double skWaveletZLevelOneD;
    
    
    
    
    
    
    
    public FeaturesVector(){
        
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getVarianceX() {
        return varianceX;
    }

    public void setVarianceX(double varianceX) {
        this.varianceX = varianceX;
    }

    public double getVarianceY() {
        return varianceY;
    }

    public void setVarianceY(double varianceY) {
        this.varianceY = varianceY;
    }

    public double getVarianceZ() {
        return varianceZ;
    }

    public void setVarianceZ(double varianceZ) {
        this.varianceZ = varianceZ;
    }

    public double getMeanX() {
        return meanX;
    }

    public void setMeanX(double meanX) {
        this.meanX = meanX;
    }

    public double getMeanY() {
        return meanY;
    }

    public void setMeanY(double meanY) {
        this.meanY = meanY;
    }

    public double getMeanZ() {
        return meanZ;
    }

    public void setMeanZ(double meanZ) {
        this.meanZ = meanZ;
    }

    public double getStandardDeviationX() {
        return standardDeviationX;
    }

    public void setStandardDeviationX(double standardDeviationX) {
        this.standardDeviationX = standardDeviationX;
    }

    public double getStandardDeviationY() {
        return standardDeviationY;
    }

    public void setStandardDeviationY(double standardDeviationY) {
        this.standardDeviationY = standardDeviationY;
    }

    public double getStandardDeviationZ() {
        return standardDeviationZ;
    }

    public void setStandardDeviationZ(double standardDeviationZ) {
        this.standardDeviationZ = standardDeviationZ;
    }

    public double getAverageResultantAcceleration() {
        return averageResultantAcceleration;
    }

    public void setAverageResultantAcceleration(double averageResultantAcceleration) {
        this.averageResultantAcceleration = averageResultantAcceleration;
    }

    public double getMeanXYZ() {
        return meanXYZ;
    }

    public void setMeanXYZ(double meanXYZ) {
        this.meanXYZ = meanXYZ;
    }

    public double getDistributionX() {
        return distributionX;
    }

    public void setDistributionX(double distributionX) {
        this.distributionX = distributionX;
    }

    public double getDistributionY() {
        return distributionY;
    }

    public void setDistributionY(double distributionY) {
        this.distributionY = distributionY;
    }

    public double getDistributionZ() {
        return distributionZ;
    }

    public void setDistributionZ(double distributionZ) {
        this.distributionZ = distributionZ;
    }

    public double getCovarianceXY() {
        return covarianceXY;
    }

    public void setCovarianceXY(double covarianceXY) {
        this.covarianceXY = covarianceXY;
    }

    public double getCovarianceYZ() {
        return covarianceYZ;
    }

    public void setCovarianceYZ(double covarianceYZ) {
        this.covarianceYZ = covarianceYZ;
    }

    public double getCovarianceZX() {
        return covarianceZX;
    }

    public void setCovarianceZX(double covarianceZX) {
        this.covarianceZX = covarianceZX;
    }

    public double getZeroCrossingRateX() {
        return zeroCrossingRateX;
    }

    public void setZeroCrossingRateX(double zeroCrossingRateX) {
        this.zeroCrossingRateX = zeroCrossingRateX;
    }

    public double getZeroCrossingRateY() {
        return zeroCrossingRateY;
    }

    public void setZeroCrossingRateY(double zeroCrossingRateY) {
        this.zeroCrossingRateY = zeroCrossingRateY;
    }

    public double getZeroCrossingRateZ() {
        return zeroCrossingRateZ;
    }

    public void setZeroCrossingRateZ(double zeroCrossingRateZ) {
        this.zeroCrossingRateZ = zeroCrossingRateZ;
    }

    public double getFftEnergyX() {
        return fftEnergyX;
    }

    public void setFftEnergyX(double fftEnergyX) {
        this.fftEnergyX = fftEnergyX;
    }

    public double getFftEnergyY() {
        return fftEnergyY;
    }

    public void setFftEnergyY(double fftEnergyY) {
        this.fftEnergyY = fftEnergyY;
    }

    public double getFftEnergyZ() {
        return fftEnergyZ;
    }

    public void setFftEnergyZ(double fftEnergyZ) {
        this.fftEnergyZ = fftEnergyZ;
    }

    public double getFftEntropyX() {
        return fftEntropyX;
    }

    public void setFftEntropyX(double fftEntropyX) {
        this.fftEntropyX = fftEntropyX;
    }

    public double getFftEntropyY() {
        return fftEntropyY;
    }

    public void setFftEntropyY(double fftEntropyY) {
        this.fftEntropyY = fftEntropyY;
    }

    public double getFftEntropyZ() {
        return fftEntropyZ;
    }

    public void setFftEntropyZ(double fftEntropyZ) {
        this.fftEntropyZ = fftEntropyZ;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public double getComplexity() {
        return complexity;
    }

    public void setComplexity(double complexity) {
        this.complexity = complexity;
    }

    public double getMobility() {
        return mobility;
    }

    public void setMobility(double mobility) {
        this.mobility = mobility;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

	// add 8 feature Nhacld 20170802
    
    public double getParX() {
        return parX;
    }

    public void setParX(double parX) {
        this.parX = parX;
    }
    //
    public double getParY() {
        return parY;
    }

    public void setParY(double parY) {
        this.parY = parY;
    }
    //
    public double getParZ() {
        return parZ;
    }
//
    public void setParZ(double parZ) {
        this.parZ = parZ;
    }
    public double getsmaX() {
        return smaX;
    }

    public void setsmaX(double smaX) {
        this.smaX = smaX;
    }
  //
    public double getsmaY() {
        return smaY;
    }

    public void setsmaY(double smaY) {
        this.smaY = smaY;
    }
    //
    public double getsmaZ() {
        return smaZ;
    }

    public void setsmaZ(double smaZ) {
        this.smaZ = smaZ;
    }
    //
    public double getsma() {
        return sma;
    }

    public void setsma(double sma) {
        this.sma = sma;
    }
    
    //
    public double getdsvm() {
        return dsvm;
    }

    public void setdsvm(double dsvm) {
        this.dsvm = dsvm;
    }
    // setActivity
    
    public void setActivityX(double activityX) {
        this.activityX = activityX;
    }
    
    public void setActivityY(double activityY) {
        this.activityY = activityY;
    }
    
    public void setActivityZ(double activityZ) {
        this.activityZ = activityZ;
    }
    
    public void setActivityPhi(double activityPhi) {
        this.activityPhi = activityPhi;
    }
    
    public void setActivityTheta(double activityTheta) {
        this.activityTheta = activityTheta;
    }
    
    // Mobility
    public void setMobilityX(double mobilityX) {
        this.mobilityX = mobilityX;
    }
    
    public void setMobilityY(double mobilityY) {
        this.mobilityY = mobilityY;
    }
    
    public void setMobilityZ(double mobilityZ) {
        this.mobilityZ = mobilityZ;
    }
    
    public void setMobilityPhi(double mobilityPhi) {
        this.mobilityPhi = mobilityPhi;
    }
    
    public void setMobilityTheta(double mobilityTheta) {
        this.mobilityTheta = mobilityTheta;
    }
    
    // Complextity
    public void setComplexityX(double complexityX) {
        this.complexityX = complexityX;
    }
    
    public void setComplexityY(double complexityY) {
        this.complexityY = complexityY;
    }
    
    public void setComplexityZ(double complexityZ) {
        this.complexityZ = complexityZ;
    }
    
    public void setComplexityPhi(double complexityPhi) {
        this.complexityPhi = complexityPhi;
    }
    
    public void setComplexityTheta(double complexityTheta) {
        this.complexityTheta = complexityTheta;
    }
    // 
    public void setMeanPhi(double meanPhi) {
        this.meanPhi = meanPhi;
    }
    //
    
    public void setMeanTheta(double meanTheta) {
    	this.meanTheta = meanTheta;
    
    }
    //
    public void setvariancePhi(double variancePhi) {
        this.variancePhi = variancePhi;
    }
    //
    
    public void setvarianceTheta(double varianceTheta) {
    	this.varianceTheta = varianceTheta;
    }
    
    // 
    public void setigPHI(double igPHI) {
        this.igPHI = igPHI;
    }
    //
    
    public void setigTHETA(double igTHETA) {
    	this.igTHETA = igTHETA;
    }
    // get function
    
// getActivity
    
    public double getActivityX() {
        return activityX;
    }
    
    public double getActivityY() {
        return activityY;
    }
    
    public double getActivityZ() {
        return activityZ;
    }
    
    public double getActivityPhi() {
        return activityPhi;
    }
    
    public double getActivityTheta() {
        return activityTheta;
    }
    
    // Mobility
    public double getMobilityX() {
        return mobilityX;
    }
    
    public double getMobilityY() {
        return mobilityY;
    }
    
    public double getMobilityZ() {
        return mobilityZ;
    }
    
    public double getMobilityPhi() {
        return mobilityPhi;
    }
    
    public double getMobilityTheta() {
        return mobilityTheta;
    }
    
    // Complextity
    public double getComplexityX() {
        return complexityX;
    }
    
    public double getComplexityY() {
        return complexityY;
    }
    
    public double getComplexityZ() {
        return complexityZ;
    }
    
    public double getComplexityPhi() {
        return complexityPhi;
    }
    
    public double getComplexityTheta() {
        return complexityTheta;
    }
    // 
    public double getMeanPhi() {
        return meanPhi;
    }
    //
    
    public double getMeanTheta() {
    	return meanTheta;
    
    }
    //
    public double getvariancePhi() {
        return variancePhi;
    }
    //
    
    public double getvarianceTheta() {
    	return varianceTheta;
    }
    
    // 
    public double getigPHI() {
        return igPHI;
    }
    //
    
    public double getigTHETA() {
    	return igTHETA;
    }
    
    //
    
    
//    public void setFftEnergyX(double fftEnergyX) {
//        this.fftEnergyX = fftEnergyX;
//    }
//
//    public double getFftEnergyY() {
//        return fftEnergyY;
//    }
    

    
    public double getVarPhiOren() {
    	return varPhiOren;
    }
    //
    
    public double getAvaPhiOren() {
    	return avaPhiOren;
    }
    //
    
    public double getVarLamdaOren() {
    	return varLamdaOren;
    }
    //
    
    public double getAvaLamdaOren() {
    	return avaLamdaOren;
    }
    //
    
    public void setvarPhiOren(double varPhiOren) {
        this.varPhiOren = varPhiOren;
    }
    public void setavaPhiOren(double avaPhiOren) {
        this.avaPhiOren = avaPhiOren;
    }
    public void setvarLamdaOren(double varLamdaOren) {
        this.varLamdaOren = varLamdaOren;
    }
    public void setavaLamdaOren(double avaLamdaOren) {
        this.avaLamdaOren = avaLamdaOren;
    }
    
    //MavWaveletXLevelOneA AvpWaveletXLevelOneA
    
    public double getMavWaveletXLevelOneA() {
    	return mavWaveletXLevelOneA;
    }
    
    public double getMavWaveletXLevelOneD() {
    	return mavWaveletXLevelOneD;
    }
    
    public double getAvpWaveletXLevelOneA() {
    	return avpWaveletXLevelOneA;
    }
    
    public double getAvpWaveletXLevelOneD() {
    	return avpWaveletXLevelOneD;
    }
    
    public double getSkWaveletXLevelOneA() {
    	return skWaveletXLevelOneA;
    }
    
    public double getSkWaveletXLevelOneD() {
    	return skWaveletXLevelOneD;
    }
    
    // for Y 
    public double getMavWaveletYLevelOneA() {
    	return mavWaveletYLevelOneA;
    }
    
    public double getMavWaveletYLevelOneD() {
    	return mavWaveletYLevelOneD;
    }
    
    public double getAvpWaveletYLevelOneA() {
    	return avpWaveletYLevelOneA;
    }
    
    public double getAvpWaveletYLevelOneD() {
    	return avpWaveletYLevelOneD;
    }
    
    public double getSkWaveletYLevelOneA() {
    	return skWaveletYLevelOneA;
    }
    
    public double getSkWaveletYLevelOneD() {
    	return skWaveletYLevelOneD;
    }
    
 // for Z 
    public double getMavWaveletZLevelOneA() {
    	return mavWaveletZLevelOneA;
    }
    
    public double getMavWaveletZLevelOneD() {
    	return mavWaveletZLevelOneD;
    }
    
    public double getAvpWaveletZLevelOneA() {
    	return avpWaveletZLevelOneA;
    }
    
    public double getAvpWaveletZLevelOneD() {
    	return avpWaveletZLevelOneD;
    }
    
    public double getSkWaveletZLevelOneA() {
    	return skWaveletZLevelOneA;
    }
    
    public double getSkWaveletZLevelOneD() {
    	return skWaveletZLevelOneD;
    }
    

    
    
    public void setMavWaveletXLevelOneA(double mavWaveletXLevelOneA) {
        this.mavWaveletXLevelOneA = mavWaveletXLevelOneA;
    }
    public void setMavWaveletXLevelOneD(double mavWaveletXLevelOneD) {
        this.mavWaveletXLevelOneD = mavWaveletXLevelOneD;
    }
    public void setAvpWaveletXLevelOneA(double avpWaveletXLevelOneA) {
        this.avpWaveletXLevelOneA = avpWaveletXLevelOneA;
    }
    public void setAvpWaveletXLevelOneD(double avpWaveletXLevelOneD) {
        this.avpWaveletXLevelOneD = avpWaveletXLevelOneD;
    }
    
    public void setSkWaveletXLevelOneA(double skWaveletXLevelOneA) {
        this.skWaveletXLevelOneA = skWaveletXLevelOneA;
    }
    public void setSkWaveletXLevelOneD(double skWaveletXLevelOneD) {
        this.skWaveletXLevelOneD = skWaveletXLevelOneD;
    }
    
    //for Y
    
    public void setMavWaveletYLevelOneA(double mavWaveletYLevelOneA) {
        this.mavWaveletYLevelOneA = mavWaveletYLevelOneA;
    }
    public void setMavWaveletYLevelOneD(double mavWaveletYLevelOneD) {
        this.mavWaveletYLevelOneD = mavWaveletYLevelOneD;
    }
    public void setAvpWaveletYLevelOneA(double avpWaveletYLevelOneA) {
        this.avpWaveletYLevelOneA = avpWaveletYLevelOneA;
    }
    public void setAvpWaveletYLevelOneD(double avpWaveletYLevelOneD) {
        this.avpWaveletYLevelOneD = avpWaveletYLevelOneD;
    }
    
    public void setSkWaveletYLevelOneA(double skWaveletYLevelOneA) {
        this.skWaveletYLevelOneA = skWaveletYLevelOneA;
    }
    public void setSkWaveletYLevelOneD(double skWaveletYLevelOneD) {
        this.skWaveletYLevelOneD = skWaveletYLevelOneD;
    }
    
 //for Z
    
    public void setMavWaveletZLevelOneA(double mavWaveletZLevelOneA) {
        this.mavWaveletZLevelOneA = mavWaveletZLevelOneA;
    }
    public void setMavWaveletZLevelOneD(double mavWaveletZLevelOneD) {
        this.mavWaveletZLevelOneD = mavWaveletZLevelOneD;
    }
    public void setAvpWaveletZLevelOneA(double avpWaveletZLevelOneA) {
        this.avpWaveletZLevelOneA = avpWaveletZLevelOneA;
    }
    public void setAvpWaveletZLevelOneD(double avpWaveletZLevelOneD) {
        this.avpWaveletZLevelOneD = avpWaveletZLevelOneD;
    }
    
    public void setSkWaveletZLevelOneA(double skWaveletZLevelOneA) {
        this.skWaveletZLevelOneA = skWaveletZLevelOneA;
    }
    public void setSkWaveletZLevelOneD(double skWaveletZLevelOneD) {
        this.skWaveletZLevelOneD = skWaveletZLevelOneD;
    }
    
    
}


