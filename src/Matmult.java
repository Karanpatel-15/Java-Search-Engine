public class Matmult {

    public static Double[][] mult_matrix(Double[][] matrixA, Double[][] matrixB){

        if(matrixA[0].length != matrixB.length)
            return null;

        Double[][] multipliedMatrix = new Double[matrixA.length][matrixB[0].length];
        double sum = 0;
        for(int rowA = 0; rowA < matrixA.length; rowA++){
            for(int columnB = 0; columnB < matrixB[0].length; columnB++){
                for(int columnA = 0; columnA < matrixA[0].length; columnA++){
                    sum += matrixA[rowA][columnA] * matrixB[columnA][columnB];
                }
                multipliedMatrix[rowA][columnB] = sum;
                sum = 0;
            }
        }

        return multipliedMatrix;
    }


    public static double euclidean_dist(Double[][] vectorA, Double[][] vectorB){

        if(vectorA[0].length != vectorB[0].length)
            return 0;

        double underRootSum = 0;

        for(int i = 0; i < vectorA[0].length; i++){
            underRootSum+= Math.pow((vectorA[0][i] - vectorB[0][i]), 2);
        }

        return Math.pow(underRootSum, 0.5);
    }

}
